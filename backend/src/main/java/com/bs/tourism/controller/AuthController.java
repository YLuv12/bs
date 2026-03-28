package com.bs.tourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bs.tourism.entity.UserInfo;
import com.bs.tourism.mapper.UserInfoMapper;
import com.bs.tourism.util.JwtUtil;
import com.bs.tourism.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    private static final String APP_ID = "wxa583d9148609c549";
    private static final String APP_SECRET = "03cb97ebdb39da2b7b0deac4662e5f66";

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @PostMapping("/wechat")
    public Map<String, Object> wechatLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String nickName = params.get("nickName");
        String avatarUrl = params.get("avatarUrl");
        Map<String, Object> result = new HashMap<>();
        try {
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APP_ID +
                    "&secret=" + APP_SECRET +
                    "&js_code=" + code +
                    "&grant_type=authorization_code";
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> wxResult = mapper.readValue(response, Map.class);
            String openid = (String) wxResult.get("openid");
            if (openid == null) {
                result.put("code", 400);
                result.put("message", "微信登录失败");
                return result;
            }
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("wx_openid", openid);
            UserInfo user = userInfoMapper.selectOne(wrapper);
            if (user == null) {
                user = new UserInfo();
                user.setWxOpenid(openid);
                user.setWxNickname(nickName);
                user.setWxAvatar(avatarUrl);
                user.setUsername("wx_" + openid.substring(0, 10));
                user.setPassword("");
                user.setLoginType(1);
                userInfoMapper.insert(user);
            } else {
                user.setWxNickname(nickName);
                user.setWxAvatar(avatarUrl);
                userInfoMapper.updateById(user);
            }
            String token = JwtUtil.createToken(user.getId().intValue());
            result.put("code", 200);
            result.put("token", token);
            result.put("user", user);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败");
        }
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");

        Map<String, Object> result = new HashMap<>();

        if (phone == null || phone.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            result.put("code", 400);
            result.put("message", "账号或密码错误");
            return result;
        }

        try {
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            UserInfo user = userInfoMapper.selectOne(wrapper);

            if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
                result.put("code", 400);
                result.put("message", "账号或密码错误");
                return result;
            }

            String token = JwtUtil.createToken(user.getId().intValue());

            result.put("code", 200);
            result.put("token", token);
            result.put("user", user);

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败，请稍后重试");
        }

        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        Map<String, Object> result = new HashMap<>();
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            result.put("code", 400);
            result.put("message", "请输入正确的手机号");
            return result;
        }
        if (!PasswordUtil.isValidPassword(password)) {
            result.put("code", 400);
            result.put("message", "密码需8-16位，包含大小写字母和数字");
            return result;
        }
        try {
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            if (userInfoMapper.selectOne(wrapper) != null) {
                result.put("code", 400);
                result.put("message", "该手机号已注册");
                return result;
            }
            UserInfo user = new UserInfo();
            user.setPhone(phone);
            user.setPassword(PasswordUtil.encode(password));
            user.setUsername(phone);
            user.setWxNickname("用户" + phone.substring(7));
            user.setLoginType(2);
            userInfoMapper.insert(user);
            String token = JwtUtil.createToken(user.getId().intValue());
            result.put("code", 200);
            result.put("token", token);
            result.put("user", user);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "注册失败，请稍后重试");
        }
        return result;
    }
}
