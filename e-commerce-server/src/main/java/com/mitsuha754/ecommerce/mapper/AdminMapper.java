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
     * 获取有效订单(未发货,已发货)
     * @return 订单
     */
    List<AdminOrderListVO> showAllOrders();
}
