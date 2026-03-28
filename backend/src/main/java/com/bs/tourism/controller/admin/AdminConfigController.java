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
@RequestMapping("/admin/config")
public class AdminConfigController {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String module) {

        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like("config_key", keyword)
                    .or().like("config_name", keyword));
        }

        if (StringUtils.hasText(module)) {
            queryWrapper.eq("module", module);
        }

        queryWrapper.orderByAsc("module").orderByAsc("id");
        Page<SysConfig> result = sysConfigMapper.selectPage(page, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<SysConfig> detail(@PathVariable Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        if (config == null || config.getDeleteTime() != null) {
            return Result.error(404, "配置不存在");
        }
        return Result.success(config);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysConfig config, HttpServletRequest request) {
        if (!StringUtils.hasText(config.getConfigKey())) {
            return Result.error(400, "配置键不能为空");
        }
        if (!StringUtils.hasText(config.getConfigValue())) {
            return Result.error(400, "配置值不能为空");
        }

        QueryWrapper<SysConfig> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("config_key", config.getConfigKey());
        checkWrapper.isNull("delete_time");
        if (sysConfigMapper.selectCount(checkWrapper) > 0) {
            return Result.error(400, "配置键已存在");
        }

        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        sysConfigMapper.insert(config);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("系统配置");
        log.setOperation("新增配置：" + config.getConfigKey());
        log.setTargetId(config.getId());
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysConfig config, HttpServletRequest request) {
        SysConfig exist = sysConfigMapper.selectById(config.getId());
        if (exist == null || exist.getDeleteTime() != null) {
            return Result.error(404, "配置不存在");
        }

        config.setUpdateTime(new Date());
        sysConfigMapper.updateById(config);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("系统配置");
        log.setOperation("修改配置：" + config.getConfigKey());
        log.setTargetId(config.getId());
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

        UpdateWrapper<SysConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        sysConfigMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("系统配置");
        log.setOperation("删除配置：" + ids);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("删除成功");
    }

    @GetMapping("/modules")
    public Result<List<String>> getModules() {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");
        queryWrapper.select("DISTINCT module");
        queryWrapper.isNotNull("module");

        List<SysConfig> configs = sysConfigMapper.selectList(queryWrapper);
        List<String> modules = new ArrayList<>();
        for (SysConfig config : configs) {
            if (config.getModule() != null && !modules.contains(config.getModule())) {
                modules.add(config.getModule());
            }
        }

        return Result.success(modules);
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
