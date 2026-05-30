package com.fandian.service;

import com.fandian.model.entity.User;
import com.fandian.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务 - 处理登录注册逻辑
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final WechatService wechatService;

    public UserService(UserRepository userRepository, WechatService wechatService) {
        this.userRepository = userRepository;
        this.wechatService = wechatService;
    }

    /**
     * 微信登录：通过 code 换取 OpenID，新用户自动注册，老用户更新登录时间
     * @param code   微信临时登录凭证
     * @param nickname 用户昵称
     * @return OpenID，失败返回 null
     */
    public String login(String code, String nickname) {
        // Step 1: 调用微信接口换取 OpenID
        String openid = wechatService.getOpenId(code);
        if (openid == null) {
            log.warn("微信登录失败: code 无效");
            return null;
        }

        // Step 2: 查找或创建用户
        Optional<User> existing = userRepository.findById(openid);
        if (existing.isPresent()) {
            User user = existing.get();
            user.setLastLoginTime(LocalDateTime.now());
            user.setNickname(nickname);
            userRepository.save(user);
            log.info("老用户登录: openid={}", openid);
        } else {
            User user = new User(openid);
            user.setNickname(nickname);
            userRepository.save(user);
            log.info("新用户注册: openid={}", openid);
        }

        return openid;
    }

    /**
     * 验证 OpenID 是否有效（用户是否存在）
     */
    public boolean isValidUser(String openid) {
        return openid != null && userRepository.existsById(openid);
    }
}
