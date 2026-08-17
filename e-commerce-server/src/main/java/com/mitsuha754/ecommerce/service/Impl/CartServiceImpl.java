package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.mapper.CartMapper;
import com.mitsuha754.ecommerce.dto.CartDTO;
import com.mitsuha754.ecommerce.vo.CartVO;
import com.mitsuha754.ecommerce.service.CartService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;

    @Override
    public void createCart(CartDTO cartDTO) {
        cartMapper.addCart(cartDTO);
    }

    @Override
    public List<CartVO> getCarts(String userName) {
        return cartMapper.getCarts(userName);
    }

    @Override
    public void updateCart(Long id, String userName) {
        cartMapper.updateCart(id, userName);
    }
}
