package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.dto.DeactivateDTO;
import com.mitsuha754.ecommerce.dto.UpdatePasswordDTO;
import com.mitsuha754.ecommerce.dto.UserProfileUpdateDTO;
import com.mitsuha754.ecommerce.interceptor.LoginInterceptor;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.service.AuthService;
import com.mitsuha754.ecommerce.service.UserService;
import com.mitsuha754.ecommerce.util.CookieUtils;
import com.mitsuha754.ecommerce.vo.UserProfileVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AuthService authService;

    @PostMapping("/showBalance")
    public R<BigDecimal> showBalance() {
        // 余额归属当前登录用户，不信任前端传入的用户名
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        return R.ok(userService.getBalance(userName));
    }

    // ==================== 用户中心 ====================

    /**
     * 查询个人资料（不含密码）
     */
    @GetMapping("/user/profile")
    public R<UserProfileVO> getProfile() {
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        return R.ok(userService.getProfile(userName));
    }

    /**
     * 修改个人资料（姓名/手机号/邮箱/常用地址）
     */
    @PostMapping("/user/profile/update")
    public R<?> updateProfile(@Valid @RequestBody UserProfileUpdateDTO dto) {
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        userService.updateProfile(userName, dto);
        return R.ok("资料已保存");
    }

    /**
     * 修改密码（必须提供原密码）
     * 修改成功后建议前端退出登录重新登录
     */
    @PostMapping("/user/password/update")
    public R<?> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto) {
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        userService.updatePassword(userName, dto);
        return R.ok("密码修改成功，请重新登录");
    }

    /**
     * 注销账号（需要密码确认）
     * 注销后无法登录，同时清理 refreshToken
     */
    @PostMapping("/user/deactivate")
    public R<?> deactivate(@Valid @RequestBody DeactivateDTO dto,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        String userName = LoginInterceptor.requireCurrentUser().getUserName();
        userService.deactivate(userName, dto.getPassword());

        // 顺带销毁 refreshToken，避免注销后还能用旧凭证续期
        String refreshToken = CookieUtils.getCookie(request, "refresh_token");
        if (StringUtils.hasText(refreshToken)) {
            authService.logout(refreshToken);
        }
        CookieUtils.removeCookie(response, "refresh_token", CookieUtils.isHttps(request));

        return R.ok("账号已注销，感谢使用");
    }

}
