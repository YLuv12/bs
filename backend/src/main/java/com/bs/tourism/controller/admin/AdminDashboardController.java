package com.bs.tourism.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bs.tourism.common.Result;
import com.bs.tourism.entity.Comment;
import com.bs.tourism.entity.Feedback;
import com.bs.tourism.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<com.bs.tourism.entity.UserInfo> userQuery = new QueryWrapper<>();
        userQuery.isNull("delete_time");
        Long userCount = userInfoMapper.selectCount(userQuery);
        stats.put("userCount", userCount);

        QueryWrapper<com.bs.tourism.entity.ScenicSpot> scenicQuery = new QueryWrapper<>();
        scenicQuery.isNull("delete_time");
        Long scenicCount = scenicSpotMapper.selectCount(scenicQuery);
        stats.put("scenicCount", scenicCount);

        QueryWrapper<com.bs.tourism.entity.Food> foodQuery = new QueryWrapper<>();
        foodQuery.isNull("delete_time");
        Long foodCount = foodMapper.selectCount(foodQuery);
        stats.put("foodCount", foodCount);

        QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
        commentQuery.isNull("delete_time");
        Long commentCount = commentMapper.selectCount(commentQuery);
        stats.put("commentCount", commentCount);

        QueryWrapper<Comment> pendingCommentQuery = new QueryWrapper<>();
        pendingCommentQuery.isNull("delete_time");
        pendingCommentQuery.eq("status", 1);
        Long pendingCommentCount = commentMapper.selectCount(pendingCommentQuery);
        stats.put("pendingCommentCount", pendingCommentCount);

        QueryWrapper<Feedback> pendingFeedbackQuery = new QueryWrapper<>();
        pendingFeedbackQuery.isNull("delete_time");
        pendingFeedbackQuery.eq("status", 0);
        Long pendingFeedbackCount = feedbackMapper.selectCount(pendingFeedbackQuery);
        stats.put("pendingFeedbackCount", pendingFeedbackCount);

        return Result.success(stats);
    }
}
