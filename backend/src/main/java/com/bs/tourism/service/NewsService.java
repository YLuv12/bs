package com.bs.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bs.tourism.entity.News;

import java.util.List;

public interface NewsService extends IService<News> {

    List<News> getNewsList(int page, int pageSize);

    News getNewsById(Long id);

    boolean deleteNews(Long id, Long authorId);

    List<News> getMyNewsList(Long authorId, int page, int pageSize);

    void updateReadCount(Long id);
}