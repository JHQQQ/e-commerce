package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.dto.UserPayDTO;
import com.mitsuha754.ecommerce.entity.Order;
import com.mitsuha754.ecommerce.vo.MerchantOrderVO;
import com.mitsuha754.ecommerce.vo.MerchantSettleVO;
import com.mitsuha754.ecommerce.vo.OrderConfirmVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {

    /**
     * 预览订单
     */
    List<OrderConfirmVO> getOrderConfirm(@Param("userName")String userName);

    /**
     * 提交订单
     */
    void  generateOrder(@Param("Order") Order order);

    /**
     * 显示用户订单信息
     */
    List<Order> getOrders(@Param("userName")String userName);

    /**
     * 显示用户所有订单号
     */
    List<String> getOrderIds(@Param("userName")String userName);
    /**
     * 更改订单状态
     */
    void updateOrderStatus(@Param("orderNo")String orderNo, @Param("status")int status);

    /**
     * 校验订单是否存在
     */
    Boolean checkOrderStatus(@Param("orderNo")String orderNo);

    /**
     * 原子操作：扣减库存并校验库存充足
     * @return 影响行数（>0=成功，0=库存不足）
     */
    int deductStockWithCheck(@Param("orderNo") String orderNo);

    /**
     * 校验订单所有明细库存是否充足；返回不足的商品名列表
     */
    List<String> checkAllStock(@Param("orderNo") String orderNo);

    /**
     * 支付后订单取消恢复库存
     */
    void recoverStock(@Param("orderNo")String orderNo);

    /**
     * 支付后取消订单恢复用户余额
     */
    void reconvertBalance(@Param("orderNo")String orderNo);

    /**
     * 查询订单状态
     */
    Integer orderStatus(@Param("orderNo")String orderNo);

    /**
     * 查询所有待支付订单号列表（用于定时关闭超时订单）
     */
    List<String> listPendingPayOrderNos();

    /**
     * 根据订单号查询用户名
     * @param orderNo 订单号
     * @return 用户名
     */
    String selectUserName(@Param("orderNo")String orderNo);

    /**
     * 检查是否达到收货标准
     */
    boolean checkReceivable(@Param("userPayDTO") UserPayDTO userPayDTO);

    /**
     * 确认收货
     *
     * @return 影响行数（带 status=2 条件，并发/重复提交时只有一个能成功）
     */
    int receiveOrder(@Param("orderNo")String orderNo);

    // ==================== 商家结算（打款） ====================

    /**
     * 统计该订单需要结算给各商家的金额（按 商品 → 商家 汇总）
     */
    List<MerchantSettleVO> selectSettlementByOrder(@Param("orderNo") String orderNo);

    /**
     * 标记订单已结算（只对 settle_time 为空的有效，保证不会重复打款）
     *
     * @return 影响行数
     */
    int markSettled(@Param("orderNo") String orderNo);

    // ==================== 商家端订单管理 ====================

    /**
     * 查询包含本店商品的订单（含本店商品小计、件数）
     *
     * @param merchantId 商家用户ID
     * @param status     订单状态过滤，null 表示查询全部状态
     */
    List<MerchantOrderVO> selectMerchantOrders(@Param("merchantId") Long merchantId,
                                               @Param("status") Integer status);

    /**
     * 统计订单中属于本店的商品明细数量（商家操作订单前的归属校验）
     *
     * @return 0 表示该订单与本店无关
     */
    int countMerchantOrderItems(@Param("orderNo") String orderNo,
                                @Param("merchantId") Long merchantId);
}