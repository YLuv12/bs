package com.bs.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.entity.ScenicSpot;
import com.bs.tourism.mapper.ScenicSpotMapper;
import com.bs.tourism.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ScenicSpotServiceImpl implements ScenicSpotService {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Override
    public IPage<ScenicSpot> getScenicSpotList(int page, int size, String region, String type, String sortBy,
            String keyword) {
        QueryWrapper<ScenicSpot> wrapper = new QueryWrapper<>();

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

        if (type != null && !type.isEmpty()) {
            Integer typeValue = getTypeValue(type);
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
                case "distance":
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

        IPage<ScenicSpot> scenicSpotPage = new Page<>(page, size);
        IPage<ScenicSpot> result = scenicSpotMapper.selectPage(scenicSpotPage, wrapper);

        for (ScenicSpot spot : result.getRecords()) {
            spot.setAvgScore(calculateAvgScore(spot.getTotalScore(), spot.getScoreCount()));
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
            case "red":
                return 1;
            case "nature":
                return 2;
            case "ethnic":
                return 3;
            case "other":
                return 4;
            default:
                return null;
        }
    }

    @Override
    public ScenicSpot getScenicSpotById(Long id) {
        return scenicSpotMapper.selectById(id);
    }

    @Override
    public List<ScenicSpot> getHotScenicSpots(int limit) {
        QueryWrapper<ScenicSpot> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("visit_count").last("LIMIT " + limit);
        return scenicSpotMapper.selectList(wrapper);
    }

    @Override
    public List<ScenicSpot> searchScenicSpots(String keyword) {
        QueryWrapper<ScenicSpot> wrapper = new QueryWrapper<>();
        wrapper.like("name_cn", keyword).or().like("name_en", keyword).or().like("desc_cn", keyword).or()
                .like("desc_en", keyword);
        return scenicSpotMapper.selectList(wrapper);
    }

    @Override
    public void updateVisitCount(Long id) {
        ScenicSpot scenicSpot = scenicSpotMapper.selectById(id);
        if (scenicSpot != null) {
            Long currentCount = scenicSpot.getVisitCount();
            scenicSpot.setVisitCount(currentCount != null ? currentCount + 1 : 1L);
            scenicSpotMapper.updateById(scenicSpot);
        }
    }

    @Override
    public void updateCollectCount(Long id, int delta) {
        ScenicSpot scenicSpot = scenicSpotMapper.selectById(id);
        if (scenicSpot != null) {
            Long currentCount = scenicSpot.getCollectCount();
            scenicSpot.setCollectCount(currentCount != null ? currentCount + delta : (long) delta);
            scenicSpotMapper.updateById(scenicSpot);
        }
    }

    @Override
    public void updateScore(Long id, Integer score) {
        ScenicSpot scenicSpot = scenicSpotMapper.selectById(id);
        if (scenicSpot != null) {
            BigDecimal totalScore = scenicSpot.getTotalScore();
            Integer scoreCount = scenicSpot.getScoreCount();

            totalScore = totalScore != null ? totalScore.add(new BigDecimal(score)) : new BigDecimal(score);
            scoreCount = scoreCount != null ? scoreCount + 1 : 1;

            scenicSpot.setTotalScore(totalScore);
            scenicSpot.setScoreCount(scoreCount);
            scenicSpotMapper.updateById(scenicSpot);
        }
    }
}
