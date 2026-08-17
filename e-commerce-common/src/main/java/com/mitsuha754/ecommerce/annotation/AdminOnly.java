package com.mitsuha754.ecommerce.annotation;

import java.lang.annotation.*;

/**
 * 管理员专属注解
 * 加在controller类上：该类所有接口都需要管理员
 * 加在controller方法上：仅该接口需要管理员
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminOnly {

}