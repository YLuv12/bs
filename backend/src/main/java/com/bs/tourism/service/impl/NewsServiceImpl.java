package com.bs.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bs.tourism.entity.News;
import com.bs.tourism.mapper.NewsMapper;
import com.bs.tourism.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public List<News> getNewsList(int page, int pageSize) {
        Page<News> pageParam = new Page<>(page, pageSize);
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("sort", "create_time");
        return newsMapper.selectPage(pageParam, wrapper).getRecords();
    }

    @Override
    public News getNewsById(Long id) {
        News news = newsMapper.selectById(id);
        if (news != null) {
            news.setReadCount(news.getReadCount() + 1);
            newsMapper.updateById(news);
        }
        return news;
    }

    @Override
    public boolean deleteNews(Long id, Long authorId) {
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("author_id", authorId);
        News news = newsMapper.selectOne(wrapper);
        if (news != null) {
            news.setStatus(3);
            return newsMapper.updateById(news) > 0;
        }
        return false;
    }

    @Override
    public List<News> getMyNewsList(Long authorId, int page, int pageSize) {
        Page<News> pageParam = new Page<>(page, pageSize);
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", authorId);
        wrapper.in("status", 1, 3);
        wrapper.orderByDesc("create_time");
        return newsMapper.selectPage(pageParam, wrapper).getRecords();
    }

    @Override
    public void updateReadCount(Long id) {
        News news = newsMapper.selectById(id);
        if (news != null) {
            news.setReadCount(news.getReadCount() + 1);
            newsMapper.updateById(news);
        }
    }
}
