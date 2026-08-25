package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.dto.LoginDTO;
import com.mitsuha754.ecommerce.exception.BusinessException;

import com.mitsuha754.ecommerce.mapper.AuthMapper;
import com.mitsuha754.ecommerce.dto.RegisterDTO;
import com.mitsuha754.ecommerce.entity.User;
import com.mitsuha754.ecommerce.vo.UserVO;
import com.mitsuha754.ecommerce.service.AuthService;
import com.mitsuha754.ecommerce.util.LoginUtil;
import com.mitsuha754.ecommerce.util.RegisterValidatorUtil;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@ConfigurationProperties(prefix = "jwt")
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Resource
    private AuthMapper authMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private LoginUtil loginUtil;

    // ---- 登录失败限流配置 ----
    private static final String LOGIN_FAIL_KEY = "login:fail:";
    private static final String LOGIN_LOCK_KEY = "login:lock:";
    private static final int MAX_FAIL_TIMES = 5;           // 累计失败次数阈值（达到后开始锁定）
    private static final long FAIL_EXPIRE_SEC = 600;       // 失败计数窗口：10 分钟（之后失败计数清零）
    // 动态锁定时间档位：第 5 次锁 1 分钟，第 6 次锁 5 分钟，第 7 次锁 15 分钟，第 8 次起锁 30 分钟
    // 下标即"失败次数"，超过末尾档用最后一档
    private static final long[] LOCK_TIERS_SEC = {
            0L, 0L, 0L, 0L, 0L,   // 失败 1~4 次：不锁定
            60L,                  // 失败 5 次：锁 1 分钟
            300L,                 // 失败 6 次：锁 5 分钟
            900L,                 // 失败 7 次：锁 15 分钟
            1800L                 // 失败 8 次及以上：锁 30 分钟
    };

    /**
     * 根据失败次数计算本次应锁定的秒数（动态/递增）
     */
    private long lockSecondsFor(int failCount) {
        if (failCount < LOCK_TIERS_SEC.length) {
            return LOCK_TIERS_SEC[failCount];
        }
        // 超过档位上限，用最大档
        return LOCK_TIERS_SEC[LOCK_TIERS_SEC.length - 1];
    }

    /**
     * 读取锁定剩余秒数（供提示"还要等多久"）；未锁定返回 0
     */
    private long remainingLockSeconds(String key) {
        try {
            Long ttl = redisTemplate.getExpire(LOGIN_LOCK_KEY + key, TimeUnit.SECONDS);
            return ttl > 0 ? ttl : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 检查目标用户名是否被锁定
     */
    private void checkLocked(String key) {
        if (redisTemplate.hasKey(LOGIN_LOCK_KEY + key)) {
            throw new BusinessException("登录失败次数过多，请 " + fmtRemaining(remainingLockSeconds(key)) + " 后再试");
        }
        int failCount = getFailCount(key);
        long lockSec = lockSecondsFor(failCount);
        if (lockSec > 0) {
            lockUser(key, lockSec);
            throw new BusinessException("登录失败次数过多，已锁定，请 " + fmtRemaining(lockSec) + " 后再试");
        }
    }

    private String fmtRemaining(long sec) {
        long min = sec / 60;
        long s = sec % 60;
        if (min > 0) return min + " 分钟 " + s + " 秒";
        return sec + " 秒";
    }

    /**
     * 当前失败计数（无则 0）
     */
    private int getFailCount(String key) {
        Object val = redisTemplate.opsForValue().get(LOGIN_FAIL_KEY + key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return 0;
    }


    /**
     * 记录一次登录失败；达到阈值则按动态档位锁定
     */
    private void recordFail(String key) {
        try {
            Long count = redisTemplate.opsForValue().increment(LOGIN_FAIL_KEY + key);
            // 首次设置过期时间（失败计数窗口）
            if (count != null && count == 1) {
                redisTemplate.expire(LOGIN_FAIL_KEY + key, FAIL_EXPIRE_SEC, TimeUnit.SECONDS);
            }
            if (count != null && count >= MAX_FAIL_TIMES) {
                long lockSec = lockSecondsFor((int) count.longValue());
                if (lockSec > 0) {
                    lockUser(key, lockSec);
                }
            }
        } catch (Exception e) {
            log.error("记录登录失败次数异常", e);
        }
    }

    /**
     * 按指定秒数锁定用户名
     */
    private void lockUser(String key, long lockSeconds) {
        try {
            redisTemplate.opsForValue().set(LOGIN_LOCK_KEY + key, 1, lockSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("锁定用户异常", e);
        }
    }

    /**
     * 登录成功清空失败计数与锁定
     */
    private void clearFail(String key) {
        try {
            redisTemplate.delete(LOGIN_FAIL_KEY + key);
            redisTemplate.delete(LOGIN_LOCK_KEY + key);
        } catch (Exception e) {
            log.error("清除登录限流计数异常", e);
        }
    }

    @Override
    public UserVO login(LoginDTO loginDTO) {
        String userName = loginDTO.getUserName();
        // 1. 限流：检查是否已锁定 / 是否已达失败阈值
        checkLocked(userName);

        //  根据用户名查用户
        User user = authMapper.login(userName);

        //  用户不存在
        if (user == null) {
            recordFail(userName);
            throw new BusinessException("用户名或密码错误");
        }

        //  判断账号状态
        if (user.getStatus() == 0) {
            recordFail(userName);
            throw new BusinessException("账号已禁用，请联系管理员");
        }
        if (user.getStatus() == 2) {
            recordFail(userName);
            throw new BusinessException("账号状态异常，请联系管理员");
        }

        //  校验密码（BCrypt）
        boolean passOk = BCrypt.checkpw(loginDTO.getPassword(), user.getPassword());
        if (!passOk) {
            recordFail(userName);
            throw new BusinessException("用户名或密码错误");
        }

        // 登录成功，清空限流计数
        clearFail(userName);

        //  登录成功 返回 VO
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getId());
        userVO.setUserName(user.getUserName());
        userVO.setBalance(user.getBalance());
        userVO.setRole(user.getRole());

        // 把用户信息放到 JWT 里
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userVO.getUserName());
        map.put("role", userVO.getRole());

        String accessToken = loginUtil.createToken(map);
        // 返回VO给前端
        userVO.setAccessToken(accessToken);

        //RefreshToken存入Redis
        if(loginDTO.getIsRemember()){
            String refreshToken = loginUtil.generateRefreshToken();
            int expireTime =loginUtil.getRefreshExpire();
            try {
                redisTemplate.opsForValue()
                        .set(
                                String.valueOf(refreshToken),
                                user.getId(),
                                expireTime,
                                TimeUnit.SECONDS
                        );
                userVO.setRefreshToken(refreshToken);
            } catch (Exception e) {
                // Redis 不可用：本次无法记住登录，但不阻断正常登录
                log.error("记住登录写入Redis失败（不影响本次登录）", e);
            }
        }
        log.info("{}登录, 身份:{}", userVO.getUserName(),userVO.getRole());

        return userVO;
    }


    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        RegisterValidatorUtil.validate(registerDTO);        //校验表单
        boolean exists = authMapper.checkUsername(registerDTO.getUserName());   //判断用户名是否存在
        if(exists){
            throw new BusinessException("用户名已存在");
        }
        String flag = BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt());    //加密存到数据库
        authMapper.register(registerDTO.getUserName(), flag);
        log.info("{}注册", registerDTO.getUserName());
    }

    @Override
    public UserVO refreshToken(String refreshToken) {
        UserVO userVO = new UserVO();
        Long expire = redisTemplate.getExpire(refreshToken);
        //过期时间>0并且存在（getExpire 为 null 表示 key 不存在或 Redis 异常）
        if (expire > 0){
            Integer userId = (Integer)redisTemplate.opsForValue().get(refreshToken);
            User user = authMapper.login(authMapper.getUserNameById(userId));
            userVO.setUserId(userId);
            userVO.setUserName(user.getUserName());
            userVO.setRole(user.getRole());
            userVO.setBalance(user.getBalance());

            Map<String, Object> map = new HashMap<>();
            map.put("userName", user.getUserName());
            map.put("role", user.getRole());
            //创建新的accessToken实现续期
            String newAccessToken = loginUtil.createToken(map);
            userVO.setAccessToken(newAccessToken);
            userVO.setRefreshToken(refreshToken);
            log.info("{}通过token直接登录, 身份:{}, token:{}", userVO.getUserName(),userVO.getRole(),userVO.getAccessToken());
        }else{
            return null;
        }
        return userVO;
    }

    @Override
    public void logout(String refreshToken){
        try {
            redisTemplate.delete(refreshToken);
        } catch (Exception e) {
            // Redis 不可用时，退出登录不阻断（cookie 仍会被清除）
            log.error("退出登录删除Redis token失败", e);
        }
    }

}
