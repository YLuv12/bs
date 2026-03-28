package com.bs.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("scenic_spot")
public class ScenicSpot implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name_cn")
    private String nameCn;

    @TableField("name_en")
    private String nameEn;

    @TableField("desc_cn")
    private String descCn;

    @TableField("desc_en")
    private String descEn;

    @TableField("strategy_cn")
    private String strategyCn;

    @TableField("strategy_en")
    private String strategyEn;

    @TableField("address_cn")
    private String addressCn;

    @TableField("address_en")
    private String addressEn;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @TableField("open_time_cn")
    private String openTimeCn;

    @TableField("open_time_en")
    private String openTimeEn;

    @TableField("ticket_info_cn")
    private String ticketInfoCn;

    @TableField("ticket_info_en")
    private String ticketInfoEn;

    private String phone;

    @TableField("play_time_cn")
    private String playTimeCn;

    @TableField("play_time_en")
    private String playTimeEn;

    @TableField("voice_url")
    private String voiceUrl;

    private Integer type;

    @TableField("type_cn")
    private String typeCn;

    @TableField("type_en")
    private String typeEn;

    @TableField("grade_cn")
    private String gradeCn;

    @TableField("grade_en")
    private String gradeEn;

    @TableField("total_score")
    private BigDecimal totalScore;

    @TableField("score_count")
    private Integer scoreCount;

    @TableField(exist = false)
    private BigDecimal avgScore;

    @TableField("visit_count")
    private Long visitCount;

    @TableField("collect_count")
    private Long collectCount;

    @TableField("cover_img")
    private String coverImg;

    private String imgs;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("delete_time")
    private Date deleteTime;
}
