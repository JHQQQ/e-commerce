package com.mitsuha754.ecommerce.dto;

import com.mitsuha754.ecommerce.vo.OrderConfirmVO;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemDataDTO {
    private String userName;
    private String orderNo;
    private List<OrderConfirmVO> list;
}
