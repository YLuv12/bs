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
@TableName("food")
public class Food implements Serializable {
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

    @TableField("merchant_name_cn")
    private String merchantNameCn;

    @TableField("merchant_name_en")
    private String merchantNameEn;

    @TableField("signature_dish_cn")
    private String signatureDishCn;

    @TableField("signature_dish_en")
    private String signatureDishEn;

    @TableField("per_capita")
    private BigDecimal perCapita;

    @TableField("per_capita_cn")
    private String perCapitaCn;

    @TableField("per_capita_en")
    private String perCapitaEn;

    @TableField("business_time_cn")
    private String businessTimeCn;

    @TableField("business_time_en")
    private String businessTimeEn;

    @TableField("address_cn")
    private String addressCn;

    @TableField("address_en")
    private String addressEn;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String phone;

    @TableField("park_info_cn")
    private String parkInfoCn;

    @TableField("park_info_en")
    private String parkInfoEn;

    private Integer type;

    @TableField("type_cn")
    private String typeCn;

    @TableField("type_en")
    private String typeEn;

    @TableField("service_type")
    private Integer serviceType;

    @TableField("service_type_cn")
    private String serviceTypeCn;

    @TableField("service_type_en")
    private String serviceTypeEn;

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
