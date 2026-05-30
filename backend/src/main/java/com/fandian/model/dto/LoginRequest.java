package com.fandian.model.dto;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求 - 接收微信小程序传来的临时 code
 */
public class LoginRequest {

    @NotBlank(message = "code不能为空")
    private String code;

    @NotBlank(message = "nickname不能为空")
    private String nickname;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
