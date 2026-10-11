package com.mitsuha754.ecommerce.service.Impl;

import com.mitsuha754.ecommerce.dto.ApplyMerchantDTO;
import com.mitsuha754.ecommerce.dto.MerchantUpdateDTO;
import com.mitsuha754.ecommerce.entity.MerchantUpdate;
import com.mitsuha754.ecommerce.entity.Shop;
import com.mitsuha754.ecommerce.enums.ResultCodeEnum;
import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
import com.mitsuha754.ecommerce.mapper.MerchantMapper;
import com.mitsuha754.ecommerce.mapper.UserMapper;
import com.mitsuha754.ecommerce.service.MerchantService;
import com.mitsuha754.ecommerce.vo.MerchantStatusVO;
import com.mitsuha754.ecommerce.vo.MerchantVO;
import com.mitsuha754.ecommerce.vo.UserLoginVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Service
public class MerchantServiceImpl implements MerchantService {

    /** 商家状态：审核中 */
    private static final int STATUS_PENDING = 0;
    /** 商家状态：已通过 */
    private static final int STATUS_APPROVED = 1;
    /** 商家状态：已拒绝 */
    private static final int STATUS_REJECTED = 2;
    /** 商家状态：已撤销 */
    private static final int STATUS_REVOKED = 3;

    /** 资料变更申请：待审核 */
    private static final int APPLY_PENDING = 0;

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public MerchantStatusVO getStatus() {
        UserLoginVO current = LoginInterceptor.requireCurrentUser();
        Long userId = current.getUserId().longValue();

        // 角色实时查库：管理员审核通过/撤销后，旧 token 里的角色可能已过期
        String role = userMapper.getRoleByName(current.getUserName());
        if (!StringUtils.hasText(role)) {
            role = current.getRole();
        }

        MerchantVO merchant = merchantMapper.getByUserId(userId);

        MerchantStatusVO vo = new MerchantStatusVO();
        vo.setRole(role);
        vo.setMerchant(merchant);
        vo.setStatus(merchant == null ? null : merchant.getStatus());
        vo.setStatusText(statusText(vo.getStatus()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyMerchant(ApplyMerchantDTO dto) {
        UserLoginVO current = LoginInterceptor.requireCurrentUser();
        Long userId = current.getUserId().longValue();

        if ("ADMIN".equals(current.getRole())) {
            throw new BusinessException("管理员无需申请商家");
        }

        MerchantVO exist = merchantMapper.getByUserId(userId);
        if (exist != null) {
            if (Objects.equals(exist.getStatus(), STATUS_PENDING)) {
                throw new BusinessException("您的商家认证正在审核中，请耐心等待");
            }
            if (Objects.equals(exist.getStatus(), STATUS_APPROVED)) {
                throw new BusinessException("您已经是商家，无需重复申请");
            }
        }

        Shop shop = new Shop();
        shop.setOwnerId(userId);
        shop.setShopName(StringUtils.hasText(dto.getShopName())
                ? dto.getShopName().trim()
                : current.getUserName() + "的店铺");
        shop.setContactName(dto.getName());
        shop.setContactPhone(dto.getPhone());
        shop.setShopAddress(dto.getShopAddress());
        shop.setIntroduction(dto.getIntroduction());

        if (exist == null) {
            merchantMapper.insertMerchant(shop);
            log.info("用户[{}]提交商家认证申请", current.getUserName());
        } else {
            // 之前被拒绝或被撤销，允许修改资料后重新提交
            merchantMapper.updateApplication(shop);
            log.info("用户[{}]重新提交商家认证申请", current.getUserName());
        }
    }

    @Override
    public MerchantVO getCurrentMerchant() {
        Long merchantId = requireApprovedMerchantId();
        return merchantMapper.getByUserId(merchantId);
    }

    @Override
    public Long requireApprovedMerchantId() {
        UserLoginVO current = LoginInterceptor.requireCurrentUser();
        Long userId = current.getUserId().longValue();

        MerchantVO merchant = merchantMapper.getByUserId(userId);
        if (merchant == null || !Objects.equals(merchant.getStatus(), STATUS_APPROVED)) {
            throw new BusinessException(ResultCodeEnum.FORBIDDEN.getCode(),
                    "您还不是商家或商家权限已被撤销");
        }
        return userId;
    }

    /**
     * 商家状态中文描述
     */
    private String statusText(Integer status) {
        if (status == null) {
            return "未申请";
        }
        return switch (status) {
            case STATUS_PENDING -> "审核中";
            case STATUS_APPROVED -> "已通过";
            case STATUS_REJECTED -> "已拒绝";
            case STATUS_REVOKED -> "已撤销";
            default -> "未知状态";
        };
    }

    // ==================== 店铺资料变更（需管理员审核） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyUpdate(MerchantUpdateDTO dto) {
        Long userId = requireApprovedMerchantId();

        MerchantUpdate exist = merchantMapper.selectUpdateApply(userId);
        if (exist != null && Objects.equals(exist.getStatus(), APPLY_PENDING)) {
            throw new BusinessException("您有资料变更正在审核中，请耐心等待管理员审核");
        }

        // 店铺名不填则沿用当前名称，避免审核通过后店铺名被清空
        MerchantVO current = merchantMapper.getByUserId(userId);
        String shopName = StringUtils.hasText(dto.getShopName())
                ? dto.getShopName().trim()
                : (current == null ? null : current.getShopName());

        MerchantUpdate update = new MerchantUpdate();
        update.setUserId(userId);
        update.setShopName(shopName);
        update.setContactName(dto.getContactName());
        update.setContactPhone(dto.getContactPhone());
        update.setShopAddress(dto.getShopAddress());
        update.setIntroduction(dto.getIntroduction());

        if (exist == null) {
            merchantMapper.insertUpdateApply(update);
        } else {
            // 上一次被驳回 / 已通过，允许再次提交（覆盖为新的待审核申请）
            merchantMapper.updateUpdateApply(update);
        }
        log.info("商家[{}]提交店铺资料变更申请，等待管理员审核", userId);
    }

    @Override
    public MerchantUpdate getMyUpdateApply() {
        Long userId = requireApprovedMerchantId();
        return merchantMapper.selectUpdateApply(userId);
    }

}
