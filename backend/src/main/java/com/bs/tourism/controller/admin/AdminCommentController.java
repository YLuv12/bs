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
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer targetType) {

        Page<Comment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("content", keyword);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        if (targetType != null) {
            queryWrapper.eq("target_type", targetType);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Comment> result = commentMapper.selectPage(page, queryWrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (Comment comment : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", comment.getId());
            item.put("user_id", comment.getUserId());
            item.put("target_id", comment.getTargetId());
            item.put("target_type", comment.getTargetType());
            item.put("score", comment.getScore());
            item.put("content", comment.getContent());
            item.put("tags", comment.getTags());
            item.put("imgs", comment.getImgs());
            item.put("like_count", comment.getLikeCount());
            item.put("status", comment.getStatus());
            item.put("create_time", comment.getCreateTime());

            UserInfo user = userInfoMapper.selectById(comment.getUserId());
            item.put("user_name",
                    user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "未知用户");
            item.put("user_avatar", user != null ? user.getWxAvatar() : null);

            if (comment.getTargetType() == 1) {
                ScenicSpot scenic = scenicSpotMapper.selectById(comment.getTargetId());
                item.put("target_name", scenic != null ? scenic.getNameCn() : "未知景点");
            } else {
                Food food = foodMapper.selectById(comment.getTargetId());
                item.put("target_name", food != null ? food.getNameCn() : "未知美食");
            }

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

    @PostMapping("/audit")
    public Result<Void> audit(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());

        Comment comment = commentMapper.selectById(id);
        if (comment == null || comment.getDeleteTime() != null) {
            return Result.error(404, "评论不存在");
        }

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");

        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", status);
        updateWrapper.set("audit_admin_id", currentAdmin.getId());
        updateWrapper.set("audit_time", new Date());
        updateWrapper.set("update_time", new Date());
        commentMapper.update(null, updateWrapper);

        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("评论管理");
        log.setOperation(status == 2 ? "审核通过评论" : "审核拒绝评论");
        log.setTargetId(id);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success(status == 2 ? "审核通过" : "审核拒绝");
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

        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        commentMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("评论管理");
        log.setOperation("删除评论：" + ids);
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
