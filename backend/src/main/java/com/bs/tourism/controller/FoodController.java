package com.bs.tourism.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bs.tourism.entity.Food;
import com.bs.tourism.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping("/list")
    public IPage<Food> getFoodList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String keyword) {
        return foodService.getFoodList(page, size, region, category, sortBy, keyword);
    }

    @GetMapping("/detail/{id}")
    public Food getFoodById(@PathVariable Long id) {
        foodService.updateVisitCount(id);
        return foodService.getFoodById(id);
    }

    @GetMapping("/hot")
    public List<Food> getHotFoods(@RequestParam(defaultValue = "5") int limit) {
        return foodService.getHotFoods(limit);
    }

    @GetMapping("/search")
    public List<Food> searchFoods(@RequestParam String keyword) {
        return foodService.searchFoods(keyword);
    }

    @PostMapping("/score/{id}")
    public void scoreFood(@PathVariable Long id, @RequestParam Integer score) {
        foodService.updateScore(id, score);
    }
}