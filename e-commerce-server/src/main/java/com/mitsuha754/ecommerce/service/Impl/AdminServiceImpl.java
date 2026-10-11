package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.enums.OrderStatusEnum;
import com.mitsuha754.ecommerce.entity.MerchantUpdate;
import com.mitsuha754.ecommerce.entity.OrderItem;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.mapper.*;
import com.mitsuha754.ecommerce.util.RedisOrderUtil;
import com.mitsuha754.ecommerce.vo.AdminOrderListVO;
import com.mitsuha754.ecommerce.vo.MerchantUpdateVO;
import com.mitsuha754.ecommerce.vo.MerchantVO;
import com.mitsuha754.ecommerce.vo.UserQueryVO;
import com.mitsuha754.ecommerce.service.AdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    /** 商家状态：审核中 */
    private static final int MERCHANT_PENDING = 0;
    /** 商家状态：已通过 */
    private static final int MERCHANT_APPROVED = 1;
    /** 商家状态：已拒绝 */
    private static final int MERCHANT_REJECTED = 2;
    /** 商家状态：已撤销 */
    private static final int MERCHANT_REVOKED = 3;

    /** 用户角色：普通用户 */
    private static final String ROLE_USER = "USER";
    /** 用户角色：商家 */
    private static final String ROLE_MERCHANT = "MERCHANT";

    /** 资料变更申请状态：待审核 */
    private static final int APPLY_PENDING = 0;
    /** 资料变更申请状态：已通过 */
    private static final int APPLY_APPROVED = 1;
    /** 资料变更申请状态：已驳回 */
    private static final int APPLY_REJECTED = 2;

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisOrderUtil redisOrderUtil;



    @Override
    public List<UserQueryVO> getAllUsers() {
        return adminMapper.getAllUser();
    }

    @Override
    public void updateUserStatus(Integer id, int status) {
        adminMapper.updateUserStatus(id,status);
        log.info("用户{}被更改为状态{}", id, status);
    }

    @Override
    public void updateUserBalance(Integer id, BigDecimal balance) {
        adminMapper.updateUserBalance(id,balance);
        log.info("用户{}余额被改为{}", id, balance);
    }

    @Override
    public List<AdminOrderListVO> showAllOrders(Integer status) {
        return adminMapper.showAllOrders(status);
    }

    @Override
    public void toDeliver(String orderNo) {
        Integer status = orderMapper.orderStatus(orderNo);
        if (!Objects.equals(status, OrderStatusEnum.PENDING_DELIVER.getCode())) {
            throw new BusinessException("订单当前状态不可发货");
        }
        orderMapper.updateOrderStatus(orderNo, OrderStatusEnum.DELIVERED.getCode());
        log.info("订单:{}完成发货", orderNo);
    }

    @Override
    public List<OrderItem> getOrderDetail(String orderNo) {
        return orderItemMapper.getOrderItems(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        Integer status = orderMapper.orderStatus(orderNo);
        if (status == null) {
            throw new BusinessException("订单不存在");
        }
        // 与商家端保持一致：只有待付款/待发货可以取消
        if (!Objects.equals(status, OrderStatusEnum.PENDING_PAY.getCode())
                && !Objects.equals(status, OrderStatusEnum.PENDING_DELIVER.getCode())) {
            throw new BusinessException("订单当前状态不可取消");
        }
        // 待发货（已支付）订单取消时回退库存与余额
        if (Objects.equals(status, OrderStatusEnum.PENDING_DELIVER.getCode())) {
            orderMapper.recoverStock(orderNo);
            orderMapper.reconvertBalance(orderNo);
        }
        orderMapper.updateOrderStatus(orderNo, OrderStatusEnum.CANCELED.getCode());

        try {
            redisOrderUtil.deleteTempOrder(orderNo);
        } catch (Exception e) {
            log.error("取消订单后删除Redis临时订单失败，订单:{}", orderNo, e);
        }
        log.info("管理员取消订单:{}", orderNo);
    }

    // ==================== 商家管理 ====================

    @Override
    public List<MerchantVO> getMerchantList(Integer status) {
        return merchantMapper.listMerchants(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveMerchant(Long userId) {
        MerchantVO merchant = requireMerchant(userId);

        if (Objects.equals(merchant.getStatus(), MERCHANT_APPROVED)) {
            throw new BusinessException("该用户已经是商家");
        }

        merchantMapper.updateStatus(userId, MERCHANT_APPROVED);
        // 角色升级为商家，旧 token 会在下次刷新/重新登录后生效
        userMapper.updateRole(userId, ROLE_MERCHANT);
        log.info("商家申请通过：userId={}, 店铺={}", userId, merchant.getShopName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectMerchant(Long userId) {
        MerchantVO merchant = requireMerchant(userId);

        if (!Objects.equals(merchant.getStatus(), MERCHANT_PENDING)) {
            throw new BusinessException("该申请不在待审核状态");
        }

        merchantMapper.updateStatus(userId, MERCHANT_REJECTED);
        // 兜底：确保角色是普通用户
        userMapper.updateRole(userId, ROLE_USER);
        log.info("商家申请被拒绝：userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeMerchant(Long userId) {
        MerchantVO merchant = requireMerchant(userId);

        if (!Objects.equals(merchant.getStatus(), MERCHANT_APPROVED)) {
            throw new BusinessException("该用户当前不是商家，无需撤销");
        }

        merchantMapper.updateStatus(userId, MERCHANT_REVOKED);
        userMapper.updateRole(userId, ROLE_USER);
        // 撤销权限后其名下商品全部下架，避免继续对外售卖
        int offShelf = productMapper.updateMerchantProductOffShelf(userId);
        log.info("撤销商家权限：userId={}, 同时下架商品{}件", userId, offShelf);
    }

    /**
     * 查询商家记录，不存在则抛业务异常
     */
    private MerchantVO requireMerchant(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        MerchantVO merchant = merchantMapper.getByUserId(userId);
        if (merchant == null) {
            throw new BusinessException("商家申请记录不存在");
        }
        return merchant;
    }

    // ==================== 商家资料变更审核 ====================

    @Override
    public List<MerchantUpdateVO> getMerchantUpdateList(Integer status) {
        return merchantMapper.listUpdateApplies(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveMerchantUpdate(Long userId) {
        MerchantUpdate apply = requireUpdateApply(userId);

        // 先把申请内容写回商家资料，再把申请标记为已通过
        merchantMapper.applyUpdateToMerchant(userId);
        merchantMapper.updateApplyStatus(userId, APPLY_APPROVED);
        log.info("商家[{}]资料变更审核通过，最新店铺名：{}", userId, apply.getShopName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectMerchantUpdate(Long userId) {
        requireUpdateApply(userId);
        merchantMapper.updateApplyStatus(userId, APPLY_REJECTED);
        log.info("商家[{}]资料变更被驳回", userId);
    }

    /**
     * 查询待审核的资料变更申请，不存在或已审核则抛业务异常
     */
    private MerchantUpdate requireUpdateApply(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        MerchantUpdate apply = merchantMapper.selectUpdateApply(userId);
        if (apply == null) {
            throw new BusinessException("资料变更申请不存在");
        }
        if (!Objects.equals(apply.getStatus(), APPLY_PENDING)) {
            throw new BusinessException("该申请不在待审核状态");
        }
        return apply;
    }
}
