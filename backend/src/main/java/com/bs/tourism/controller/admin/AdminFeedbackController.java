package com.bs.tourism.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.common.Result;
import com.bs.tourism.entity.*;
import com.bs.tourism.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type) {

        Page<Feedback> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("content", keyword);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        if (type != null) {
            queryWrapper.eq("type", type);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Feedback> result = feedbackMapper.selectPage(page, queryWrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (Feedback feedback : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", feedback.getId());
            item.put("user_id", feedback.getUserId());
            item.put("type", feedback.getType());
            item.put("content", feedback.getContent());
            item.put("contact", feedback.getContact());
            item.put("imgs", feedback.getImgs());
            item.put("status", feedback.getStatus());
            item.put("handle_result", feedback.getHandleResult());
            item.put("handle_time", feedback.getHandleTime());
            item.put("create_time", feedback.getCreateTime());

            UserInfo user = userInfoMapper.selectById(feedback.getUserId());
            item.put("user_name",
                    user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "未知用户");

            records.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null || feedback.getDeleteTime() != null) {
            return Result.error(404, "反馈不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", feedback.getId());
        data.put("user_id", feedback.getUserId());
        data.put("type", feedback.getType());
        data.put("content", feedback.getContent());
        data.put("contact", feedback.getContact());
        data.put("imgs", feedback.getImgs());
        data.put("status", feedback.getStatus());
        data.put("handle_result", feedback.getHandleResult());
        data.put("handle_time", feedback.getHandleTime());
        data.put("create_time", feedback.getCreateTime());

        UserInfo user = userInfoMapper.selectById(feedback.getUserId());
        data.put("user_name",
                user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "未知用户");

        return Result.success(data);
    }

    @PostMapping("/handle")
    public Result<Void> handle(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        String handleResult = (String) params.get("handle_result");

        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null || feedback.getDeleteTime() != null) {
            return Result.error(404, "反馈不存在");
        }

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");

        UpdateWrapper<Feedback> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", status);
        updateWrapper.set("handle_result", handleResult);
        updateWrapper.set("handle_admin_id", currentAdmin.getId());
        updateWrapper.set("handle_time", new Date());
        feedbackMapper.update(null, updateWrapper);

        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("反馈管理");
        log.setOperation(status == 1 ? "处理反馈" : "驳回反馈");
        log.setTargetId(id);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success(status == 1 ? "处理成功" : "驳回成功");
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Object idObj = params.get("id");
        List<Long> ids = new ArrayList<>();

        if (idObj instanceof List) {
            for (Object obj : (List<?>) idObj) {
                ids.add(Long.valueOf(obj.toString()));
            }
        } else {
            ids.add(Long.valueOf(idObj.toString()));
        }

        UpdateWrapper<Feedback> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        feedbackMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("反馈管理");
        log.setOperation("删除反馈：" + ids);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("删除成功");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
