package com.mitsuha754.ecommerce.annotation;

import java.lang.annotation.*;

/**
 * 商家专属注解
 * 加在controller类上：该类所有接口都需要「已通过审核的商家」
 * 加在controller方法上：仅该接口需要商家
 *
 * 注意：切面只校验 token 中的角色为 MERCHANT，
 * 商家资格是否仍然有效（未被撤销）由 MerchantService 查询商家表二次确认。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MerchantOnly {

}
