package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.dto.CartDTO;
import com.mitsuha754.ecommerce.vo.CartVO;
import com.mitsuha754.ecommerce.service.CartService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private CartService cartService;

    @PostMapping("/add")
    public R<?> add(@RequestBody CartDTO cartDTO){
        // 购物车归属当前登录用户，防止伪造他人用户名
        cartDTO.setUserName(LoginInterceptor.requireCurrentUser().getUserName());
        cartService.createCart(cartDTO);
        return R.ok();
    }

    @PostMapping("/list")
    public R<List<CartVO>> getCartList(){
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        return R.ok(cartService.getCarts(userName));
    }

    @PostMapping("/delete")
    public R<?> deleteCart(@RequestParam Long id){
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        cartService.updateCart(id, userName);
        return R.ok();
    }
}
