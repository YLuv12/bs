package com.bs.tourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bs.tourism.entity.Footprint;
import com.bs.tourism.entity.ScenicSpot;
import com.bs.tourism.entity.Food;
import com.bs.tourism.mapper.FootprintMapper;
import com.bs.tourism.mapper.ScenicSpotMapper;
import com.bs.tourism.mapper.FoodMapper;
import com.bs.tourism.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/footprint")
public class FootprintController {

    @Autowired
    private FootprintMapper footprintMapper;

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private FoodMapper foodMapper;

    @GetMapping("/list")
    public Map<String, Object> getList(@RequestHeader("Authorization") String authorization) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authorization.replace("Bearer ", "");
            Integer userId = JwtUtil.getUserId(token);
            if (userId == null) {
                result.put("code", 401);
                result.put("message", "token无效");
                return result;
            }

            QueryWrapper<Footprint> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.isNull("delete_time");
            wrapper.orderByDesc("browse_time");

            List<Footprint> list = footprintMapper.selectList(wrapper);

            List<Map<String, Object>> records = new ArrayList<>();
            for (Footprint footprint : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", footprint.getId());
                item.put("target_id", footprint.getTargetId());
                item.put("target_type", footprint.getTargetType());
                item.put("browse_time", footprint.getBrowseTime());

                if (footprint.getTargetType() == 1) {
                    ScenicSpot spot = scenicSpotMapper.selectById(footprint.getTargetId());
                    if (spot != null) {
                        item.put("target_name_cn", spot.getNameCn());
                        item.put("target_name_en", spot.getNameEn());
                        item.put("target_image", spot.getCoverImg());
                    }
                } else {
                    Food food = foodMapper.selectById(footprint.getTargetId());
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

            QueryWrapper<Footprint> wrapper = new QueryWrapper<>();
            wrapper.eq("id", id).eq("user_id", userId);
            footprintMapper.delete(wrapper);

            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/clear")
    public Map<String, Object> clearAll(@RequestHeader("Authorization") String authorization) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authorization.replace("Bearer ", "");
            Integer userId = JwtUtil.getUserId(token);
            if (userId == null) {
                result.put("code", 401);
                result.put("message", "token无效");
                return result;
            }

            QueryWrapper<Footprint> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            footprintMapper.delete(wrapper);

            result.put("code", 200);
            result.put("message", "清空成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "清空失败: " + e.getMessage());
        }
        return result;
    }
}
