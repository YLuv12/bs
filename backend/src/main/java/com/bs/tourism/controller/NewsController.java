package com.bs.tourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.entity.News;
import com.bs.tourism.mapper.NewsMapper;
import com.bs.tourism.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsMapper newsMapper;

    @PostMapping("/publish")
    public Map<String, Object> publish(@RequestBody News news,
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

            news.setStatus(1);
            news.setSort(0);
            news.setReadCount(0L);
            newsMapper.insert(news);

            result.put("code", 200);
            result.put("message", "发布成功");
            result.put("data", news);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "发布失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/my-list")
    public Map<String, Object> getMyList(@RequestParam(defaultValue = "1") Integer page,
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

            Page<News> pageParam = new Page<>(page, pageSize);
            QueryWrapper<News> wrapper = new QueryWrapper<>();
            wrapper.eq("author_id", userId);
            wrapper.in("status", 1, 3);
            wrapper.orderByDesc("create_time");

            Page<News> newsPage = newsMapper.selectPage(pageParam, wrapper);

            result.put("code", 200);
            result.put("records", newsPage.getRecords());
            result.put("total", newsPage.getTotal());
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

            QueryWrapper<News> wrapper = new QueryWrapper<>();
            wrapper.eq("id", id).eq("author_id", userId);
            News news = newsMapper.selectOne(wrapper);
            if (news != null) {
                news.setStatus(3);
                newsMapper.updateById(news);
            }

            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            Page<News> pageParam = new Page<>(page, pageSize);
            QueryWrapper<News> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1);
            wrapper.orderByDesc("sort", "create_time");

            Page<News> newsPage = newsMapper.selectPage(pageParam, wrapper);

            result.put("code", 200);
            result.put("records", newsPage.getRecords());
            result.put("total", newsPage.getTotal());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            News news = newsMapper.selectById(id);
            if (news != null) {
                news.setReadCount(news.getReadCount() + 1);
                newsMapper.updateById(news);
            }

            result.put("code", 200);
            result.put("data", news);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
}