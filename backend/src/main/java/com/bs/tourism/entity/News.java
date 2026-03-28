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
@TableName("news")
public class News implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title_cn")
    private String titleCn;

    @TableField("title_en")
    private String titleEn;

    @TableField("content_cn")
    private String contentCn;

    @TableField("content_en")
    private String contentEn;

    @TableField("cover_img")
    private String coverImg;

    @TableField("author_id")
    private Long authorId;

    @TableField("author_name")
    private String authorName;

    @TableField("audit_admin_id")
    private Long auditAdminId;

    @TableField("audit_time")
    private Date auditTime;

    @TableField("read_count")
    private Long readCount;

    private Integer status;

    private Integer sort;

    private String tags;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("delete_time")
    private Date deleteTime;
}
