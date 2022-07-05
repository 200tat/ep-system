package com.primeton.commom.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("permission")
@ApiModel("权限表，又名角色-菜单关联表")
public class Permission implements Serializable {
    @TableId
    @ApiModelProperty("权限id")
    String permissionId;

    @ApiModelProperty("角色id")
    String roleId;

    @ApiModelProperty("菜单id")
    String menuId;
}
