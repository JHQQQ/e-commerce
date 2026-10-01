package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.enums.OrderStatusEnum;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.mapper.*;
import com.mitsuha754.ecommerce.util.RedisOrderUtil;
import com.mitsuha754.ecommerce.vo.AdminOrderListVO;
import com.mitsuha754.ecommerce.vo.UserQueryVO;
import com.mitsuha754.ecommerce.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private RedisOrderUtil redisOrderUtil;



    @Override
    public List<UserQueryVO> getAllUsers() {
        return adminMapper.getAllUser();
    }

    @Override
    public void updateUserStatus(Integer id, int status) {
        adminMapper.updateUserStatus(id,status);
        log.info("用户{}被更改为状态{}", id, status);
    }

    @Override
    public void updateUserBalance(Integer id, BigDecimal balance) {
        adminMapper.updateUserBalance(id,balance);
        log.info("用户{}余额被改为{}", id, balance);
    }

    @Override
    public List<AdminOrderListVO> showAllOrders() {
        return adminMapper.showAllOrders();
    }

    @Override
    public void toDeliver(String orderNo) {
        orderMapper.updateOrderStatus(orderNo, 2);
        log.info("订单:{}完成发货", orderNo);
    }

    @Override
    public List<OrderItem> getOrderDetail(String orderNo) {
        return orderItemMapper.getOrderItems(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        Integer status = orderMapper.orderStatus(orderNo);
        // 待发货（已支付）订单取消时回退库存与余额
        if (Objects.equals(status, OrderStatusEnum.PENDING_DELIVER.getCode())) {
            orderMapper.recoverStock(orderNo);
            orderMapper.reconvertBalance(orderNo);
        }
        orderMapper.updateOrderStatus(orderNo, OrderStatusEnum.CANCELED.getCode());

           redisOrderUtil.deleteTempOrder(orderNo);
        log.info("管理员取消订单:{}", orderNo);
    }

    @Override
    public void confirmMerchant(@NotNull BigInteger userId){
        try {
            adminMapper.confirmMerchant(userId);
        }catch (Exception e){
            throw new BusinessException("出现异常");
        }
    }
}
