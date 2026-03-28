package com.bs.tourism.controller;

import com.bs.tourism.entity.*;
import com.bs.tourism.mapper.*;
import com.bs.tourism.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private FootprintMapper footprintMapper;

    // 获取用户资料
    @GetMapping("/profile")
    public Map<String, Object> getUserProfile(@RequestHeader("Authorization") String authorization) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authorization.replace("Bearer ", "");
            Integer userId = JwtUtil.getUserId(token);
            if (userId == null) {
                result.put("code", 401);
                result.put("message", "token无效");
                return result;
            }
            UserInfo user = userInfoMapper.selectById(userId);
            if (user == null) {
                result.put("code", 404);
                result.put("message", "用户不存在");
                return result;
            }
            result.put("code", 200);
            result.put("user", user);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取资料失败: " + e.getMessage());
        }
        return result;
    }

    // 更新用户资料
    @PostMapping("/update")
    public Map<String, Object> updateUser(@RequestBody UserInfo userInfo, @RequestHeader("Authorization") String authorization) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authorization.replace("Bearer ", "");
            Integer userId = JwtUtil.getUserId(token);
            if (userId == null) {
                result.put("code", 401);
                result.put("message", "token无效");
                return result;
            }
            userInfo.setId(Long.valueOf(userId));
            userInfoMapper.updateById(userInfo);
            UserInfo updatedUser = userInfoMapper.selectById(userId);
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("user", updatedUser);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @PostMapping("/login")
    public UserInfo loginUser(@RequestParam String username, @RequestParam String password) {
        List<UserInfo> users = userInfoMapper.selectList(null);
        for (UserInfo user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @GetMapping("/detail/{id}")
    public UserInfo getUserById(@PathVariable Integer id) {
        return userInfoMapper.selectById(id);
    }

    @GetMapping("/collects/{userId}")
    public List<Collect> getUserCollects(@PathVariable Integer userId) {
        return collectMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Collect>().eq("user_id", userId));
    }

    @GetMapping("/comments/{userId}")
    public List<Comment> getUserComments(@PathVariable Integer userId) {
        return commentMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Comment>().eq("user_id", userId));
    }

    @GetMapping("/footprints/{userId}")
    public List<Footprint> getUserFootprints(@PathVariable Integer userId) {
        return footprintMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Footprint>().eq("user_id", userId));
    }

}