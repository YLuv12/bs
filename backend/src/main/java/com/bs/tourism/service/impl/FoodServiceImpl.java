package com.bs.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.entity.Food;
import com.bs.tourism.mapper.FoodMapper;
import com.bs.tourism.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public IPage<Food> getFoodList(int page, int size, String region, String category, String sortBy, String keyword) {
        QueryWrapper<Food> wrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like("name_cn", keyword)
                    .or().like("name_en", keyword)
                    .or().like("desc_cn", keyword)
                    .or().like("address_cn", keyword));
        }

        if (region != null && !region.isEmpty()) {
            String regionCn = getRegionCn(region);
            wrapper.like("address_cn", regionCn);
        }

        if (category != null && !category.isEmpty()) {
            Integer typeValue = getTypeValue(category);
            if (typeValue != null) {
                wrapper.eq("type", typeValue);
            }
        }

        if (sortBy != null) {
            switch (sortBy) {
                case "score":
                    wrapper.orderByDesc("total_score");
                    wrapper.orderByDesc("score_count");
                    break;
                case "visit":
                    wrapper.orderByDesc("visit_count");
                    break;
                case "collect":
                    wrapper.orderByDesc("collect_count");
                    break;
                case "price":
                    wrapper.orderByAsc("per_capita");
                    break;
                case "random":
                    wrapper.orderByAsc("RAND()");
                    break;
                default:
                    wrapper.orderByDesc("id");
                    break;
            }
        } else {
            wrapper.orderByAsc("RAND()");
        }

        IPage<Food> foodPage = new Page<>(page, size);
        IPage<Food> result = foodMapper.selectPage(foodPage, wrapper);

        for (Food food : result.getRecords()) {
            food.setAvgScore(calculateAvgScore(food.getTotalScore(), food.getScoreCount()));
        }

        return result;
    }

    private BigDecimal calculateAvgScore(BigDecimal totalScore, Integer scoreCount) {
        if (totalScore == null || scoreCount == null || scoreCount == 0) {
            return BigDecimal.ZERO;
        }
        return totalScore.divide(new BigDecimal(scoreCount), 1, RoundingMode.HALF_UP);
    }

    private String getRegionCn(String region) {
        switch (region.toLowerCase()) {
            case "youjiang":
                return "右江区";
            case "tianyang":
                return "田阳区";
            case "tiandong":
                return "田东";
            case "tianlin":
                return "田林";
            case "xilin":
                return "西林";
            case "longlin":
                return "隆林";
            case "jingxi":
                return "靖西";
            case "napo":
                return "那坡";
            case "debao":
                return "德保";
            case "leye":
                return "乐业";
            case "lingyun":
                return "凌云";
            case "pingguo":
                return "平果";
            default:
                return region;
        }
    }

    private Integer getTypeValue(String type) {
        switch (type.toLowerCase()) {
            case "specialty":
                return 1;
            case "snack":
                return 2;
            case "drink":
                return 3;
            default:
                return null;
        }
    }

    @Override
    public Food getFoodById(Long id) {
        return foodMapper.selectById(id);
    }

    @Override
    public List<Food> getHotFoods(int limit) {
        QueryWrapper<Food> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("visit_count").last("LIMIT " + limit);
        return foodMapper.selectList(wrapper);
    }

    @Override
    public List<Food> searchFoods(String keyword) {
        QueryWrapper<Food> wrapper = new QueryWrapper<>();
        wrapper.like("name_cn", keyword).or().like("name_en", keyword).or().like("desc_cn", keyword).or()
                .like("desc_en", keyword);
        return foodMapper.selectList(wrapper);
    }

    @Override
    public void updateVisitCount(Long id) {
        Food food = foodMapper.selectById(id);
        if (food != null) {
            Long currentCount = food.getVisitCount();
            food.setVisitCount(currentCount != null ? currentCount + 1 : 1L);
            foodMapper.updateById(food);
        }
    }

    @Override
    public void updateScore(Long id, Integer score) {
        Food food = foodMapper.selectById(id);
        if (food != null) {
            BigDecimal totalScore = food.getTotalScore();
            Integer scoreCount = food.getScoreCount();

            totalScore = totalScore != null ? totalScore.add(new BigDecimal(score)) : new BigDecimal(score);
            scoreCount = scoreCount != null ? scoreCount + 1 : 1;

            food.setTotalScore(totalScore);
            food.setScoreCount(scoreCount);
            foodMapper.updateById(food);
        }
    }
}
