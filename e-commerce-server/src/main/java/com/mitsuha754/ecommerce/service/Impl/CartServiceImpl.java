package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.entity.Product;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
import com.mitsuha754.ecommerce.mapper.CartMapper;
import com.mitsuha754.ecommerce.mapper.ProductMapper;
import com.mitsuha754.ecommerce.dto.CartDTO;
import com.mitsuha754.ecommerce.vo.CartVO;
import com.mitsuha754.ecommerce.vo.UserLoginVO;
import com.mitsuha754.ecommerce.service.CartService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    public void createCart(CartDTO cartDTO) {
        Product product = productMapper.selectProductById(cartDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!Objects.equals(product.getStatus(), 1) || !Objects.equals(product.getAuditStatus(), 1)) {
            throw new BusinessException("商品已下架或未通过审核");
        }
        Boolean enough = cartMapper.isStockEnough(cartDTO);
        //如果为null，代表用户还没添加过购物车，可以放行。如果不为null且商品库存<购物车已有的数量+新增的数量,代表购物车数量过多
        if (enough != null && enough.equals(Boolean.FALSE)) {
            throw new BusinessException("添加购物车数量不得大于库存");
        }

        // 商家（与用户端同一账号）不能购买自己店铺的商品
        UserLoginVO me = LoginInterceptor.requireCurrentUser();
        if (me.getUserId() != null && product.getMerchantId() != null
                && product.getMerchantId().equals(me.getUserId().longValue())) {
            throw new BusinessException("不能购买自己店铺的商品");
        }

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
