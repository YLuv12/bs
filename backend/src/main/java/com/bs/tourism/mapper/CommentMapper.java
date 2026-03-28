package com.bs.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bs.tourism.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}