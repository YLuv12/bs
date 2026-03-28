package com.bs.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("admin_operation_log")
public class AdminOperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("admin_id")
    private Long adminId;

    private String module;

    private String operation;

    @TableField("target_id")
    private Long targetId;

    private String ip;

    @TableField("create_time")
    private Date createTime;

    @TableField(exist = false)
    private String adminName;
}
