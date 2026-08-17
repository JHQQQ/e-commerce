package com.mitsuha754.ecommerce.mapper;

import com.mitsuha754.ecommerce.dto.CartDTO;
import com.mitsuha754.ecommerce.vo.CartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    /**
     * @param cartDTO 插入购物车
     */
    void addCart(@Param("Cart") CartDTO cartDTO);

    /**
     * @param userName 用户名
     * @return 展示一位用户的所有购物车
     */
    List<CartVO> getCarts(@Param("userName") String userName);

    /**
     * 用户删除购物车（同时校验用户名，防止删除他人购物车）
     */
    void updateCart(@Param("id") Long id, @Param("userName") String userName);

    /**
     * 提交订单后,删除购物车
     */
    void deleteCart(@Param("userName")String userName);

}
