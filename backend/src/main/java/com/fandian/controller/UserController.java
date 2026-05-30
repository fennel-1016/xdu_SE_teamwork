package com.fandian.controller;

import com.fandian.model.dto.LoginRequest;
import com.fandian.model.dto.LoginResponse;
import com.fandian.model.dto.Result;
import com.fandian.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户认证控制器
 * 接口: POST /api/user/login
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 微信登录接口
     * @param request { code: "wx_login_code", nickname: "用户昵称" }
     * @return { code: 200, msg: "登录成功", data: { openid: "xxx" } }
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求: code={}", request.getCode().substring(0, Math.min(10, request.getCode().length())) + "...");

        String openid = userService.login(request.getCode(), request.getNickname());

        if (openid == null) {
            return Result.fail(401, "微信登录异常，请重试");
        }

        return Result.ok("登录成功", new LoginResponse(openid));
    }
}
