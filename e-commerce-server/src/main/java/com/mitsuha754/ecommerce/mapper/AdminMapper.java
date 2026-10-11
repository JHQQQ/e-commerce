package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.vo.AdminOrderListVO;
import com.mitsuha754.ecommerce.vo.UserQueryVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AdminMapper {
    List<UserQueryVO> getAllUser();

    void updateUserStatus(@Param("id") Integer id, @Param("status") int status);

    void updateUserBalance(@Param("id") Integer id, @Param("balance") BigDecimal balance);

    /**
     * 获取订单列表
     *
     * @param status 订单状态过滤：0待付款 1待发货 2已发货 3已完成 4已取消；
     *               null 表示默认只查进行中（待发货/已发货）
     */
    List<AdminOrderListVO> showAllOrders(@Param("status") Integer status);
}
