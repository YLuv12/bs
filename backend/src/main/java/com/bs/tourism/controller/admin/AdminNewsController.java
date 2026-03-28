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
@RequestMapping("/admin/news")
public class AdminNewsController {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        Page<News> page = new Page<>(pageNum, pageSize);
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like("title_cn", keyword)
                    .or().like("author_name", keyword));
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");
        Page<News> result = newsMapper.selectPage(page, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<News> detail(@PathVariable Long id) {
        News news = newsMapper.selectById(id);
        if (news == null || news.getDeleteTime() != null) {
            return Result.error(404, "资讯不存在");
        }
        return Result.success(news);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody News news, HttpServletRequest request) {
        if (!StringUtils.hasText(news.getTitleCn())) {
            return Result.error(400, "标题不能为空");
        }
        if (!StringUtils.hasText(news.getContentCn())) {
            return Result.error(400, "内容不能为空");
        }

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");

        news.setAuthorId(currentAdmin.getId());
        news.setAuthorName(
                currentAdmin.getNickname() != null ? currentAdmin.getNickname() : currentAdmin.getUsername());
        news.setCreateTime(new Date());
        news.setUpdateTime(new Date());
        news.setReadCount(0L);
        if (news.getStatus() == null) {
            news.setStatus(1);
        }
        newsMapper.insert(news);

        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("资讯管理");
        log.setOperation("新增资讯：" + news.getTitleCn());
        log.setTargetId(news.getId());
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody News news, HttpServletRequest request) {
        News exist = newsMapper.selectById(news.getId());
        if (exist == null || exist.getDeleteTime() != null) {
            return Result.error(404, "资讯不存在");
        }

        news.setUpdateTime(new Date());
        newsMapper.updateById(news);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("资讯管理");
        log.setOperation("修改资讯：" + news.getTitleCn());
        log.setTargetId(news.getId());
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("修改成功");
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

        UpdateWrapper<News> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        newsMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("资讯管理");
        log.setOperation("删除资讯：" + ids);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("删除成功");
    }

    @PostMapping("/publish")
    public Result<Void> publish(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long id = Long.valueOf(params.get("id").toString());

        News news = newsMapper.selectById(id);
        if (news == null || news.getDeleteTime() != null) {
            return Result.error(404, "资讯不存在");
        }

        UpdateWrapper<News> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", 1);
        updateWrapper.set("update_time", new Date());
        newsMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("资讯管理");
        log.setOperation("发布资讯：" + news.getTitleCn());
        log.setTargetId(id);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("发布成功");
    }

    @PostMapping("/offline")
    public Result<Void> offline(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long id = Long.valueOf(params.get("id").toString());

        News news = newsMapper.selectById(id);
        if (news == null || news.getDeleteTime() != null) {
            return Result.error(404, "资讯不存在");
        }

        UpdateWrapper<News> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", 3);
        updateWrapper.set("update_time", new Date());
        newsMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("资讯管理");
        log.setOperation("下架资讯：" + news.getTitleCn());
        log.setTargetId(id);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("下架成功");
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
