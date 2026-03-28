package com.bs.tourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.entity.Feedback;
import com.bs.tourism.mapper.FeedbackMapper;
import com.bs.tourism.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @PostMapping("/submit")
    public Map<String, Object> submit(@RequestBody Feedback feedback,
                                      @RequestHeader("Authorization") String authorization) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authorization.replace("Bearer ", "");
            Integer userId = JwtUtil.getUserId(token);
            if (userId == null) {
                result.put("code", 401);
                result.put("message", "token无效");
                return result;
            }

            feedback.setUserId(Long.valueOf(userId));
            feedback.setStatus(0);
            feedbackMapper.insert(feedback);

            result.put("code", 200);
            result.put("message", "提交成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "提交失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestHeader("Authorization") String authorization) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authorization.replace("Bearer ", "");
            Integer userId = JwtUtil.getUserId(token);
            if (userId == null) {
                result.put("code", 401);
                result.put("message", "token无效");
                return result;
            }

            Page<Feedback> pageParam = new Page<>(page, pageSize);
            QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.orderByDesc("create_time");

            Page<Feedback> feedbackPage = feedbackMapper.selectPage(pageParam, wrapper);

            result.put("code", 200);
            result.put("records", feedbackPage.getRecords());
            result.put("total", feedbackPage.getTotal());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authorization) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authorization.replace("Bearer ", "");
            Integer userId = JwtUtil.getUserId(token);
            if (userId == null) {
                result.put("code", 401);
                result.put("message", "token无效");
                return result;
            }

            QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
            wrapper.eq("id", id).eq("user_id", userId);
            Feedback feedback = feedbackMapper.selectOne(wrapper);

            result.put("code", 200);
            result.put("data", feedback);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
}