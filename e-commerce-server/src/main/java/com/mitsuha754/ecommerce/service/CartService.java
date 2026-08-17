package com.mitsuha754.ecommerce.service;

import com.mitsuha754.ecommerce.dto.CartDTO;
import com.mitsuha754.ecommerce.vo.CartVO;

import java.util.List;


public interface CartService {
    /**
     * 添加购物车
     * @param cartDTO 前端传来的
     */
    void createCart(CartDTO cartDTO);

    /**
     * 获取用户的购物车列表
     * @param userName 根据用户名
     * @return 购物车列表
     */
    List<CartVO> getCarts(String userName);

    /**
     * 用户删除购物车
     * @param id 购物车条目ID
     * @param userName 当前登录用户
     */
    void updateCart(Long id, String userName);
}
