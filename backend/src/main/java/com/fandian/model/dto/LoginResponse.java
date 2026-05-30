package com.fandian.model.dto;

/**
 * 登录响应 - 返回 OpenID
 */
public class LoginResponse {

    private String openid;

    public LoginResponse() {}

    public LoginResponse(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
