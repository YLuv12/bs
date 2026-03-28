package com.bs.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bs.tourism.entity.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLog> {

    @Select("SELECT l.*, a.username as admin_name FROM admin_operation_log l " +
            "LEFT JOIN admin a ON l.admin_id = a.id " +
            "ORDER BY l.create_time DESC LIMIT #{offset}, #{limit}")
    List<AdminOperationLog> selectListWithAdminName(@Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM admin_operation_log")
    Long selectCountTotal();
}
