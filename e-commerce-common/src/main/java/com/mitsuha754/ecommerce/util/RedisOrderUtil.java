package com.mitsuha754.ecommerce.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 订单专用工具
 * 存：临时订单（未支付），30分钟自动过期
 */

@Component
public class RedisOrderUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 订单前缀
    private static final String ORDER_PREFIX = "order:temp:";

    // 幂等键前缀
    private static final String REQUEST_PREFIX = "order:request:";

    // 过期时间：30分钟
    private static final long EXPIRE_MINUTES = 30;

    // 幂等键过期时间：10分钟
    private static final long REQUEST_EXPIRE_MINUTES = 10;

    /**
     * 保存临时订单到 Redis
     */
    public void setTempOrder(String orderNo, Object orderInfo) {
        String key = ORDER_PREFIX + orderNo;
        redisTemplate.opsForValue().set(key, orderInfo, EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 获取临时订单
     */
    public Object getTempOrder(String orderNo) {
        String key = ORDER_PREFIX + orderNo;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除临时订单（支付成功/取消时调用）
     */
    public void deleteTempOrder(String orderNo) {
        String key = ORDER_PREFIX + orderNo;
        redisTemplate.delete(key);
    }

    /**
     * 判断临时订单是否存在
     */
    public boolean hasOrder(String orderNo) {
        String key = ORDER_PREFIX + orderNo;
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取剩余过期时间（秒）
     */
    public Long getOrderExpireSeconds(String orderNo) {
        return redisTemplate.getExpire(ORDER_PREFIX + orderNo);
    }

    /**
     * 幂等：首次记录 requestId -> orderNo，返回 true；已存在返回 false
     */
    public boolean trySetRequestId(String requestId, String orderNo) {
        String key = REQUEST_PREFIX + requestId;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, orderNo, REQUEST_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 获取 requestId 对应的订单号（重复提交时返回已有订单）
     */
    public String getRequestOrderNo(String requestId) {
        Object val = redisTemplate.opsForValue().get(REQUEST_PREFIX + requestId);
        return val == null ? null : String.valueOf(val);
    }
}