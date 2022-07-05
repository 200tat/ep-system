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
@TableName("user_role_area_shelters")
@ApiModel("用户-角色-区域/方舱关联表")
public class UserRoleAreaShelters implements Serializable {
    @ApiModelProperty("关联表2id")
    @TableId
    String associationId;

    @ApiModelProperty("角色id")
    String roleId;

    @ApiModelProperty("资源id 区域/方舱的id")
    String resourceId;

    @ApiModelProperty("资源类型：0为区域，1为方舱/防疫中心")
    Integer resourceType;

    @ApiModelProperty("参与者id")
    String participantId;
    @ApiModelProperty("1为用户")
    Integer participantType;
}
