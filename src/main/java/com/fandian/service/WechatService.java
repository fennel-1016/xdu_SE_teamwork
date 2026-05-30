package com.fandian.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * 微信服务 - 调用微信开放平台接口换取 OpenID
 */
@Service
public class WechatService {

    private static final Logger log = LoggerFactory.getLogger(WechatService.class);

    private static final String WECHAT_API_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WechatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 通过临时 code 换取用户 OpenID
     * @param jsCode 前端调用 wx.login() 获取的临时凭证
     * @return OpenID，失败返回 null
     */
    public String getOpenId(String jsCode) {
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WECHAT_API_URL, appid, secret, jsCode);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode node = objectMapper.readTree(response.getBody());

                if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                    log.error("微信接口返回错误: errcode={}, errmsg={}",
                            node.get("errcode").asInt(),
                            node.has("errmsg") ? node.get("errmsg").asText() : "未知");
                    return null;
                }

                if (node.has("openid")) {
                    return node.get("openid").asText();
                }
            }
        } catch (IOException e) {
            log.error("解析微信响应失败", e);
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
        }
        return null;
    }
}
