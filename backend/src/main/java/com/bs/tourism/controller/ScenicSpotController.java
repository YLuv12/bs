package com.bs.tourism.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bs.tourism.entity.ScenicSpot;
import com.bs.tourism.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scenic-spot")
public class ScenicSpotController {

    @Autowired
    private ScenicSpotService scenicSpotService;

    @GetMapping("/list")
    public IPage<ScenicSpot> getScenicSpotList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String keyword) {
        return scenicSpotService.getScenicSpotList(page, size, region, type, sortBy, keyword);
    }

    @GetMapping("/detail/{id}")
    public ScenicSpot getScenicSpotById(@PathVariable Long id) {
        scenicSpotService.updateVisitCount(id);
        return scenicSpotService.getScenicSpotById(id);
    }

    @GetMapping("/hot")
    public List<ScenicSpot> getHotScenicSpots(@RequestParam(defaultValue = "5") int limit) {
        return scenicSpotService.getHotScenicSpots(limit);
    }

    @GetMapping("/search")
    public List<ScenicSpot> searchScenicSpots(@RequestParam String keyword) {
        return scenicSpotService.searchScenicSpots(keyword);
    }

    @PostMapping("/collect/{id}")
    public void collectScenicSpot(@PathVariable Long id) {
        scenicSpotService.updateCollectCount(id, 1);
    }

    @DeleteMapping("/collect/{id}")
    public void cancelCollectScenicSpot(@PathVariable Long id) {
        scenicSpotService.updateCollectCount(id, -1);
    }

    @PostMapping("/score/{id}")
    public void scoreScenicSpot(@PathVariable Long id, @RequestParam Integer score) {
        scenicSpotService.updateScore(id, score);
    }
}