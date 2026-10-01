package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.dto.OrderDTO;
import com.mitsuha754.ecommerce.dto.UserPayDTO;
import com.mitsuha754.ecommerce.entity.Order;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.vo.OrderConfirmVO;
import com.mitsuha754.ecommerce.service.OrderService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/confirm")
    public R<List<OrderConfirmVO>> confirmOrder() {
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        return R.ok(orderService.getOrdersConfirm(userName));
    }

    @PostMapping("/create")
    public R<?> createOrder(@RequestBody OrderDTO orderDTO) {
        // 订单归属当前登录用户，不信任前端传入的用户名
        orderDTO.setUserName(LoginInterceptor.requireCurrentUser().getUserName());
        return R.ok(orderService.generateOrder(orderDTO));
    }

    @PostMapping("order-list")
    public R<List<Order>> getOrderList(@RequestParam
                                       @NotEmpty
                                       String userName) {
        return R.ok(orderService.showOrders(userName));
    }

    @PostMapping("/order-item-list")
    public R<Map<String,List<OrderItem>>> showOrdersItem(@RequestParam
                                                         @NotEmpty
                                                         String userName) {
        return R.ok(orderService.getOrderItems(userName));
    }

    @PostMapping("/cancel")
    public R<?> cancelOrder(@RequestParam String orderNo) {
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        orderService.cancelOrder(orderNo, userName);
        return R.ok();
    }

    @GetMapping("/detail/{orderNo}")
    public R<List<OrderItem>> getOrderDetail(@PathVariable String orderNo) {
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        return R.ok(orderService.getOrderItem(orderNo, userName));
    }

    @PostMapping("/toPay")
    public R<?> toPay(@RequestBody @Valid UserPayDTO userPayDTO) {
        orderService.toPay(userPayDTO);
        return R.ok();
    }

    @PostMapping("/received")
    public R<?> receivedOrder(@RequestBody UserPayDTO userPayDTO) {
        userPayDTO.setUserName(LoginInterceptor.requireCurrentUser().getUserName());
        orderService.receivedOrder(userPayDTO);
        return R.ok();
    }
}
