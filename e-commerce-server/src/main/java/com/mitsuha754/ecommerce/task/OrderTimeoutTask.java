package com.mitsuha754.ecommerce.task;

import com.mitsuha754.ecommerce.enums.OrderStatusEnum;
import com.mitsuha754.ecommerce.mapper.OrderMapper;
import com.mitsuha754.ecommerce.util.RedisOrderUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单超时定时关闭任务
 * 待支付订单依赖 Redis 临时订单（30分钟过期）来标记未支付状态。
 * 当 Redis 不可用或临时订单已过期但数据库订单仍处于「待支付」时，
 * 静默停留在待支付状态的订单不会被清理。此任务定期扫描数据库中的
 * 待支付订单，凡是 Redis 中已不存在临时订单的，判定为超时并置为已取消。
 */
@Slf4j
@Component
public class OrderTimeoutTask {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RedisOrderUtil redisOrderUtil;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void closeTimeoutOrders() {
        try {
            List<String> pendingNos = orderMapper.listPendingPayOrderNos();
            if (pendingNos == null || pendingNos.isEmpty()) {
                return;
            }
            for (String orderNo : pendingNos) {
                try {
                    // Redis 中仍存在临时订单 => 未超时；否则视为已超时，关闭订单
                    if (!redisOrderUtil.hasOrder(orderNo)) {
                        orderMapper.updateOrderStatus(orderNo, OrderStatusEnum.CANCELED.getCode());
                        log.info("定时任务：关闭超时未支付订单 {}", orderNo);
                    }
                } catch (Exception e) {
                    // Redis 不可用时无法判断，跳过该订单
                    log.error("定时任务判断订单超时失败，跳过订单:{}", orderNo, e);
                }
            }
        } catch (Exception e) {
            log.error("定时任务扫描待支付订单失败", e);
        }
    }
}
