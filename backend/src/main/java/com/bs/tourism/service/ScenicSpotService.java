package com.bs.tourism.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bs.tourism.entity.ScenicSpot;
import java.util.List;

public interface ScenicSpotService {
    IPage<ScenicSpot> getScenicSpotList(int page, int size, String region, String type, String sortBy, String keyword);
    ScenicSpot getScenicSpotById(Long id);
    List<ScenicSpot> getHotScenicSpots(int limit);
    List<ScenicSpot> searchScenicSpots(String keyword);
    void updateVisitCount(Long id);
    void updateCollectCount(Long id, int delta);
    void updateScore(Long id, Integer score);
}