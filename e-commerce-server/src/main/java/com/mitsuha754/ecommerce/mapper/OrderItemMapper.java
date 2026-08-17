package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.dto.OrderItemDataDTO;
import com.mitsuha754.ecommerce.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {

    /**
     * 插入订单商品
     */
    void addOrderItem(OrderItemDataDTO orderItemDataDTO);

    /**
     * 订单详情查询列表
     */
    List<OrderItem> getOrderItems(@Param("OrderNo")String orderNo);


}
