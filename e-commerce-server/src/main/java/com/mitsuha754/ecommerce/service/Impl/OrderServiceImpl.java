package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.enums.OrderStatusEnum;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.mapper.CartMapper;
import com.mitsuha754.ecommerce.mapper.OrderItemMapper;
import com.mitsuha754.ecommerce.mapper.OrderMapper;
import com.mitsuha754.ecommerce.mapper.ProductMapper;
import com.mitsuha754.ecommerce.mapper.UserMapper;
import com.mitsuha754.ecommerce.dto.OrderDTO;
import com.mitsuha754.ecommerce.dto.OrderItemDataDTO;
import com.mitsuha754.ecommerce.dto.OrderRedisDTO;
import com.mitsuha754.ecommerce.dto.UserPayDTO;
import com.mitsuha754.ecommerce.entity.Order;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.vo.OrderConfirmVO;
import com.mitsuha754.ecommerce.vo.ProductVO;
import com.mitsuha754.ecommerce.service.OrderService;
import com.mitsuha754.ecommerce.util.OrderUtil;
import com.mitsuha754.ecommerce.util.RedisOrderUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper  orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private RedisOrderUtil redisOrderUtil;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ProductMapper productMapper;


    @Override
    public List<OrderConfirmVO> getOrdersConfirm(String userName) {
        return orderMapper.getOrderConfirm(userName);
    }

    //生成订单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateOrder(OrderDTO orderDTO) {
        String requestId = orderDTO.getRequestId();

        // 幂等：同一 requestId 重复提交时，直接返回已有订单号，避免重复生成订单
        if (requestId != null && !requestId.isBlank()) {
            try {
                String existing = redisOrderUtil.getRequestOrderNo(requestId);
                if (existing != null) {
                    log.info("检测到重复提交, requestId:{}, 返回已有订单:{}", requestId, existing);
                    return existing;
                }
            } catch (Exception e) {
                // Redis 不可用时跳过幂等检查，保证订单仍可生成
                log.error("幂等检查失败（Redis不可用），继续生成订单", e);
            }
        }

        String orderId = OrderUtil.generateOrderNo();
        boolean buyNow = orderDTO.getProductId() != null;

        List<OrderConfirmVO> list;
        if (buyNow) {
            // 立即购买：单个商品，不走购物车
            list = buildBuyNowList(orderDTO);
        } else {
            list = orderMapper.getOrderConfirm(orderDTO.getUserName());
            if (list.isEmpty()) {
                throw new BusinessException("购物车为空或商品已下架，请重新选择商品");
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderConfirmVO orderConfirmVO : list) {
            totalAmount = totalAmount.add(orderConfirmVO.getTotalPrice());
        }

        //存入Redis
        OrderRedisDTO orderRedisDTO = new OrderRedisDTO();
        orderRedisDTO.setUserName(orderDTO.getUserName());
        orderRedisDTO.setStatus(OrderStatusEnum.PENDING_PAY.getCode());
        orderRedisDTO.setTotalAmount(totalAmount);

        //存入MySQL
        Order order = new Order();
        //实际支付金额暂且等于商品金额
        order.setPayAmount(totalAmount);

        order.setOrderNo(orderId);
        order.setUserName(orderRedisDTO.getUserName());
        order.setTotalAmount(totalAmount);
        order.setAddress(orderDTO.getAddress());
        order.setRemark(orderDTO.getRemark());
        order.setStatus(OrderStatusEnum.PENDING_PAY.getCode());
        orderMapper.generateOrder(order);

        // 由服务端生成订单明细，保证与订单金额一致
        OrderItemDataDTO itemData = new OrderItemDataDTO();
        itemData.setUserName(order.getUserName());
        itemData.setOrderNo(orderId);
        itemData.setList(list);
        orderItemMapper.addOrderItem(itemData);

        // 普通购物车结算才清空购物车；立即购买不影响购物车
        if (!buyNow) {
            cartMapper.deleteCart(orderDTO.getUserName());
        }
        // Redis 操作：失败不影响订单落库（MySQL），仅影响倒计时与幂等
        try {
            redisOrderUtil.setTempOrder(orderId, orderRedisDTO);
            if (requestId != null && !requestId.isBlank()) {
                redisOrderUtil.trySetRequestId(requestId, orderId);
            }
        } catch (Exception e) {
            log.error("Redis订单缓存失败（不影响订单生成）", e);
        }

        log.info("用户:{}下单,订单号:{}, 立即购买:{}", orderDTO.getUserName(), order.getOrderNo(), buyNow);
        return order.getOrderNo();
    }

    /**
     * 构建立即购买的单个商品结算明细（校验商品与库存）
     */
    private List<OrderConfirmVO> buildBuyNowList(OrderDTO orderDTO) {
        ProductVO product = productMapper.selectProductVOById(orderDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        int quantity = orderDTO.getQuantity() == null ? 1 : orderDTO.getQuantity();
        if (quantity <= 0 || product.getStock() == null || product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }
        OrderConfirmVO vo = new OrderConfirmVO();
        vo.setProductId(orderDTO.getProductId());
        vo.setProductName(product.getName());
        vo.setImage(product.getImage());
        vo.setQuantity(String.valueOf(quantity));
        vo.setPrice(product.getPrice());
        vo.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return List.of(vo);
    }

    @Override
    public List<Order> showOrders(String userName) {
        return orderMapper.getOrders(userName);
    }

    @Override
    public Long getExpireTime(String orderNo) {
        try {
            return redisOrderUtil.getOrderExpireSeconds(orderNo);
        } catch (Exception e) {
            // Redis 不可用时返回 null，前端兜底处理
            log.error("获取订单剩余时间失败，订单:{}", orderNo, e);
            return null;
        }
    }

    @Override
    public void refreshOrderTimeout(List<Order> orderNoList) {
        for(Order orderNo1:orderNoList){
            boolean exist;
            try {
                exist = redisOrderUtil.hasOrder(orderNo1.getOrderNo());
            } catch (Exception e) {
                // Redis 不可用时跳过超时处理，避免整个订单查询接口报错
                log.error("查询订单超时状态失败，Redis不可用？订单:{}", orderNo1.getOrderNo(), e);
                continue;
            }
            //redis不存在,状态为0取消
            if(!exist && Objects.equals(orderNo1.getStatus(), OrderStatusEnum.PENDING_PAY.getCode())){
                orderMapper.updateOrderStatus(orderNo1.getOrderNo(), OrderStatusEnum.CANCELED.getCode());
            }
        }
    }

    @Override
    public void addOrderItem(OrderItemDataDTO orderItemDataDTO) {
        orderItemMapper.addOrderItem(orderItemDataDTO);
    }

    @Override
    public List<OrderItem> getOrderItem(String orderNo, String userName) {
        checkOrderOwner(orderNo, userName);
        return orderItemMapper.getOrderItems(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toPay(UserPayDTO userPayDTO){

        String userName = userPayDTO.getUserName();
        String orderNo = userPayDTO.getOrderNo();

        // 1.校验订单归属当前用户（同时覆盖“订单不存在”）
        checkOrderOwner(orderNo, userName);

        // 2.校验订单当前状态必须为待支付，防止重复支付
        if (!Objects.equals(orderMapper.orderStatus(orderNo), OrderStatusEnum.PENDING_PAY.getCode())) {
            throw new BusinessException("订单状态异常，无法支付");
        }

        // 3.校验订单是否过期（Redis 里的过期时间）
        if (!redisOrderUtil.hasOrder(orderNo)) {
            throw new BusinessException("订单已超时，请重新下单");
        }

        // 3. 原子扣减余额（SQL里同时检查用户状态和余额）
        int balanceRows = userMapper.deductBalanceWithCheck(userName, orderNo);
        if (balanceRows == 0) {
            throw new BusinessException("用户状态异常或余额不足，支付失败");
        }


        // 4. 先校验所有明细库存是否充足（不足则此处整体抛错，尚未扣任何库存）
        List<String> insufficient = orderMapper.checkAllStock(orderNo);
        if (!insufficient.isEmpty()) {
            throw new BusinessException("库存不足，支付失败");
        }

        // 5. 统一原子扣减库存（每明细都带 stock>=quantity 兜底）
        int stockRows = orderMapper.deductStockWithCheck(orderNo);
        if (stockRows == 0) {
            // 说明某商品库存刚好售罄，事务回滚（余额也已回滚）
            throw new BusinessException("库存不足，支付失败");
        }

        // 6. 开始支付（修改状态为待发货）
        orderMapper.updateOrderStatus(orderNo,OrderStatusEnum.PENDING_DELIVER.getCode());

        // 7. 清理 Redis 临时数据（失败不影响已完成的支付）
        try {
            redisOrderUtil.deleteTempOrder(orderNo);
        } catch (Exception e) {
            log.error("支付成功后清理Redis临时订单失败，订单:{}", orderNo, e);
        }
        log.info("用户:{} 订单:{}完成支付",userPayDTO.getUserName(), orderNo);
    }

    @Override
    public void receivedOrder(UserPayDTO userPayDTO) {
        if(!orderMapper.checkReceiveable(userPayDTO)){
            throw new BusinessException("状态异常");
        }
        orderMapper.receiveOrder(userPayDTO.getOrderNo());
        log.info("用户:{}收货 订单号:{}",  userPayDTO.getUserName(), userPayDTO.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, String userName) {
        checkOrderOwner(orderNo, userName);
        //订单状态为未发货,取消后回退库存
        Integer status = OrderStatusEnum.PENDING_DELIVER.getCode();
        if(orderMapper.orderStatus(orderNo)
                .equals(status)){
            //回退库存
            orderMapper.recoverStock(orderNo);
            //回退余额
            orderMapper.reconvertBalance(orderNo);
            log.info("订单:{}在支付完成后取消", orderNo);
        }
        //订单状态统一改为取消
        orderMapper.updateOrderStatus(orderNo,OrderStatusEnum.CANCELED.getCode());
        try {
            redisOrderUtil.deleteTempOrder(orderNo);
        } catch (Exception e) {
            // Redis 清理失败不影响取消操作本身
            log.error("取消订单后删除Redis临时订单失败，订单:{}", orderNo, e);
        }
    }

    /**
     * 校验订单归属当前用户，防止越权操作他人订单
     */
    private void checkOrderOwner(String orderNo, String userName) {
        String owner = orderMapper.selectUserName(orderNo);
        if (owner == null || !owner.equals(userName)) {
            throw new BusinessException("无权限操作该订单");
        }
    }
}
