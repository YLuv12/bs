package com.bs.tourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bs.tourism.entity.Collect;
import com.bs.tourism.entity.ScenicSpot;
import com.bs.tourism.entity.Food;
import com.bs.tourism.mapper.CollectMapper;
import com.bs.tourism.mapper.ScenicSpotMapper;
import com.bs.tourism.mapper.FoodMapper;
import com.bs.tourism.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private FoodMapper foodMapper;

    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) String type,
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

            QueryWrapper<Collect> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            if (type != null && !type.isEmpty() && !"all".equals(type)) {
                if ("attraction".equals(type)) {
                    wrapper.eq("target_type", 1);
                } else if ("food".equals(type)) {
                    wrapper.eq("target_type", 2);
                }
            }
            wrapper.orderByDesc("create_time");

            wrapper.isNull("delete_time");

            List<Collect> list = collectMapper.selectList(wrapper);

            List<Map<String, Object>> records = new ArrayList<>();
            for (Collect collect : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", collect.getId());
                item.put("target_id", collect.getTargetId());
                item.put("target_type", collect.getTargetType());
                item.put("create_time", collect.getCreateTime());

                if (collect.getTargetType() == 1) {
                    ScenicSpot spot = scenicSpotMapper.selectById(collect.getTargetId());
                    if (spot != null) {
                        item.put("target_name_cn", spot.getNameCn());
                        item.put("target_name_en", spot.getNameEn());
                        item.put("target_image", spot.getCoverImg());
                    }
                } else {
                    Food food = foodMapper.selectById(collect.getTargetId());
                    if (food != null) {
                        item.put("target_name_cn", food.getNameCn());
                        item.put("target_name_en", food.getNameEn());
                        item.put("target_image", food.getCoverImg());
                    }
                }
                records.add(item);
            }

            result.put("code", 200);
            result.put("records", records);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id,
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

            QueryWrapper<Collect> wrapper = new QueryWrapper<>();
            wrapper.eq("id", id).eq("user_id", userId);
            collectMapper.delete(wrapper);

            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
