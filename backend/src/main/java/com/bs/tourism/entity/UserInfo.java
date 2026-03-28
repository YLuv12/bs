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
@TableName("user_info")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("wx_openid")
    private String wxOpenid;

    private String username;

    private String password;

    @TableField("wx_avatar")
    private String wxAvatar;

    @TableField("wx_nickname")
    private String wxNickname;

    private String nickname;

    private Integer age;

    private Integer gender;

    private String phone;

    private String intro;

    @TableField("login_type")
    private Integer loginType;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic(value = "NULL", delval = "NOW()")
    @TableField("delete_time")
    private Date deleteTime;
}
