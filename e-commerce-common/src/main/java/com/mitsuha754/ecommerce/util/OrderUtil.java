package com.mitsuha754.ecommerce.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单号生成工具类
 */
public class OrderUtil {

    // 雪花算法（分布式唯一ID，18位纯数字）
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    /**
     * 方案1：纯数字订单号（推荐，18位，分布式唯一）
     * 格式：145678912345678901
     */
    public static String generateOrderNo() {
        return String.valueOf(SNOWFLAKE.nextId());
    }

    /**
     * 方案2：带时间前缀的订单号（更易读，业务友好）
     * 格式：yyyyMMddHHmmss + 6位随机数 = 20位
     * 示例：20260401123045678901
     */
    public static String generateTimeOrderNo() {
        // 时间戳部分
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 随机6位
        String random = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        return time + random;
    }

    /**
     *
     *
     * 方案3：带业务前缀（最规范，电商通用）
     * 示例：ORDER_20260401_789654
     */
    public static String generateBizOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        return "ORDER_" + time + "_" + random;
    }
}