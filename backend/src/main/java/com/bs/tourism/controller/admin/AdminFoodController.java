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
@RequestMapping("/admin/food")
public class AdminFoodController {

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type) {

        Page<Food> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Food> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like("name_cn", keyword)
                    .or().like("merchant_name_cn", keyword)
                    .or().like("address_cn", keyword));
        }

        if (type != null) {
            queryWrapper.eq("type", type);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Food> result = foodMapper.selectPage(page, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<Food> detail(@PathVariable Long id) {
        Food food = foodMapper.selectById(id);
        if (food == null || food.getDeleteTime() != null) {
            return Result.error(404, "美食不存在");
        }
        return Result.success(food);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Food food, HttpServletRequest request) {
        if (!StringUtils.hasText(food.getNameCn())) {
            return Result.error(400, "美食名称不能为空");
        }

        food.setCreateTime(new Date());
        food.setUpdateTime(new Date());
        food.setVisitCount(0L);
        food.setCollectCount(0L);
        food.setTotalScore(BigDecimal.ZERO);
        food.setScoreCount(0);
        foodMapper.insert(food);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("美食管理");
        log.setOperation("新增美食：" + food.getNameCn());
        log.setTargetId(food.getId());
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody Food food, HttpServletRequest request) {
        Food exist = foodMapper.selectById(food.getId());
        if (exist == null || exist.getDeleteTime() != null) {
            return Result.error(404, "美食不存在");
        }

        food.setUpdateTime(new Date());
        foodMapper.updateById(food);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("美食管理");
        log.setOperation("修改美食：" + food.getNameCn());
        log.setTargetId(food.getId());
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

        UpdateWrapper<Food> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        foodMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("美食管理");
        log.setOperation("删除美食：" + ids);
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
