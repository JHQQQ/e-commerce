package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.dto.LoginDTO;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
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

    @Override
    public UserVO login(LoginDTO loginDTO) {
        //  根据用户名查用户
        User user = authMapper.login(loginDTO.getUserName());

        //  用户不存在
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        //  判断账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已禁用，请联系管理员");
        }
        if (user.getStatus() == 2) {
            throw new BusinessException("账号状态异常，请联系管理员");
        }

        //  校验密码（BCrypt）
        boolean passOk = BCrypt.checkpw(loginDTO.getPassword(), user.getPassword());
        if (!passOk) {
            throw new BusinessException("用户名或密码错误");
        }

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
        if (expire != null && expire > 0){
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
