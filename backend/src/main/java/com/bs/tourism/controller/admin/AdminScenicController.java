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
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/admin/scenic")
public class AdminScenicController {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type) {

        Page<ScenicSpot> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ScenicSpot> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like("name_cn", keyword)
                    .or().like("name_en", keyword)
                    .or().like("address_cn", keyword));
        }

        if (type != null) {
            queryWrapper.eq("type", type);
        }

        queryWrapper.orderByDesc("create_time");
        Page<ScenicSpot> result = scenicSpotMapper.selectPage(page, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<ScenicSpot> detail(@PathVariable Long id) {
        ScenicSpot scenic = scenicSpotMapper.selectById(id);
        if (scenic == null || scenic.getDeleteTime() != null) {
            return Result.error(404, "景点不存在");
        }
        return Result.success(scenic);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ScenicSpot scenic, HttpServletRequest request) {
        if (!StringUtils.hasText(scenic.getNameCn())) {
            return Result.error(400, "景点名称不能为空");
        }

        scenic.setCreateTime(new Date());
        scenic.setUpdateTime(new Date());
        scenic.setVisitCount(0L);
        scenic.setCollectCount(0L);
        scenic.setTotalScore(BigDecimal.ZERO);
        scenic.setScoreCount(0);
        scenicSpotMapper.insert(scenic);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("景点管理");
        log.setOperation("新增景点：" + scenic.getNameCn());
        log.setTargetId(scenic.getId());
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody ScenicSpot scenic, HttpServletRequest request) {
        ScenicSpot exist = scenicSpotMapper.selectById(scenic.getId());
        if (exist == null || exist.getDeleteTime() != null) {
            return Result.error(404, "景点不存在");
        }

        scenic.setUpdateTime(new Date());
        scenicSpotMapper.updateById(scenic);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("景点管理");
        log.setOperation("修改景点：" + scenic.getNameCn());
        log.setTargetId(scenic.getId());
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

        UpdateWrapper<ScenicSpot> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        scenicSpotMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("景点管理");
        log.setOperation("删除景点：" + ids);
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
