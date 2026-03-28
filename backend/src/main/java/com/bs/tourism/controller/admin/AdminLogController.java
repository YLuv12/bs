package com.bs.tourism.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.common.Result;
import com.bs.tourism.entity.*;
import com.bs.tourism.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/admin/log")
public class AdminLogController {

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @Autowired
    private AdminMapper adminMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Page<AdminOperationLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AdminOperationLog> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like("operation", keyword)
                    .or().like("ip", keyword));
        }

        if (StringUtils.hasText(module)) {
            queryWrapper.eq("module", module);
        }

        if (StringUtils.hasText(startDate)) {
            queryWrapper.ge("create_time", startDate + " 00:00:00");
        }

        if (StringUtils.hasText(endDate)) {
            queryWrapper.le("create_time", endDate + " 23:59:59");
        }

        queryWrapper.orderByDesc("create_time");
        Page<AdminOperationLog> result = operationLogMapper.selectPage(page, queryWrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (AdminOperationLog log : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", log.getId());
            item.put("admin_id", log.getAdminId());
            item.put("module", log.getModule());
            item.put("operation", log.getOperation());
            item.put("target_id", log.getTargetId());
            item.put("ip", log.getIp());
            item.put("create_time", log.getCreateTime());

            Admin admin = adminMapper.selectById(log.getAdminId());
            item.put("admin_name", admin != null ? admin.getUsername() : "未知");

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

    @GetMapping("/modules")
    public Result<List<String>> getModules() {
        QueryWrapper<AdminOperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT module");
        queryWrapper.isNotNull("module");

        List<AdminOperationLog> logs = operationLogMapper.selectList(queryWrapper);
        List<String> modules = new ArrayList<>();
        for (AdminOperationLog log : logs) {
            if (log.getModule() != null && !modules.contains(log.getModule())) {
                modules.add(log.getModule());
            }
        }

        return Result.success(modules);
    }
}
