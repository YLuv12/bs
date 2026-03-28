package com.bs.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("feedback")
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private Integer type;

    private String content;

    private String contact;

    private String imgs;

    private Integer status;

    @TableField("handle_admin_id")
    private Long handleAdminId;

    @TableField("handle_result")
    private String handleResult;

    @TableField("handle_time")
    private Date handleTime;

    @TableField("create_time")
    private Date createTime;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("delete_time")
    private Date deleteTime;
}
