package com.primeton.commom.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "menu")
@ApiModel("菜单")
public class Menu implements Serializable {
    @TableId
    @ApiModelProperty("菜单id")
    private String menuId;

    @ApiModelProperty("菜单名")
    private String menuName;

    @ApiModelProperty("前端路径")
    private String url;

    @ApiModelProperty("父级菜单id")
    private String parentId;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("菜单等级 1一级菜单2二级菜单3三级菜单")
    private Integer menuType;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("子菜单列表")
    @TableField(exist = false)
    private List<Menu> children;
}
