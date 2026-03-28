package com.bs.tourism.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bs.tourism.entity.Food;
import java.util.List;

public interface FoodService {
    IPage<Food> getFoodList(int page, int size, String region, String category, String sortBy, String keyword);
    Food getFoodById(Long id);
    List<Food> getHotFoods(int limit);
    List<Food> searchFoods(String keyword);
    void updateVisitCount(Long id);
    void updateScore(Long id, Integer score);
}