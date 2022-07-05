package com.primeton.commom.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@TableName(value = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户")
public class User implements Serializable {

    @ApiModelProperty("用户id")
    @TableId
    private String userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("用户名/账号")
    private String account;

    @ApiModelProperty("密码")
    private String userPassword;

    @ApiModelProperty("用户手机号")
    private String userPhone;

    @ApiModelProperty("注册时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerDate;

    @ApiModelProperty("角色id")
    private String roleId;

    @ApiModelProperty("是否删除 0未删除1删除")
    private Integer isDelete;

    @ApiModelProperty("管理的区域列表")
    @TableField(exist = false)
    private List<Area> areaList;
    @ApiModelProperty("管理的方舱")
    @TableField(exist = false)
    private Shelters shelters;

    @ApiModelProperty("对应角色")
    @TableField(exist = false)
    private Role role;
}
