package com.primeton.commom.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "patient_area_shelters")
@ApiModel("患者-地区/方舱的关联表")
public class PatientAreaShelters implements Serializable {
    @TableId
    @ApiModelProperty("关联表1id")
    String associationId;

    @ApiModelProperty("资源id（方舱/地区id）")
    String resourceId;

    @ApiModelProperty("资源类型：0为区域，1为方舱/防疫中心")
    Integer resourceType;

    @ApiModelProperty("参与者id（患者id）")
    String participantId;

    @ApiModelProperty("参与者类型 该表只有0")
    Integer participantType;

    @ApiModelProperty("患者外键")
    @TableField(exist = false)
    Patient patient;


}
