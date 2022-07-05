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

@TableName(value = "patient")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("患者")
public class Patient implements Serializable {
    @TableId
    @ApiModelProperty("患者id")
    private String patientId;

    @ApiModelProperty("患者姓名")
    private String patientName;

    @ApiModelProperty("患者性别")
    private Integer patientGender;

    @ApiModelProperty("患者籍贯")
    private String patientBirthplace;

    @ApiModelProperty("身份证号码")
    private String cardId;

    @ApiModelProperty("患者手机号")
    private String patientPhone;

    @ApiModelProperty("紧急联系人手机号")
    private String emergencyContact;

    @ApiModelProperty("阳性日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date infectionDate;

    @ApiModelProperty("行程轨迹")
    private String route;

    @ApiModelProperty("楼栋")
    private String building;

    @ApiModelProperty("转阴或死亡日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date resurrectionDate;

    @ApiModelProperty("患者状态 -1死亡0阳性1阴性")
    private Integer patientStatus;

    @ApiModelProperty("复核状态 1小区管理员录入2通过街道复核3通过行政区复合 -2街道驳回-3行政区驳回")
    private Integer recheckStatus;

    @ApiModelProperty("转移到方舱时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date transferDate;

    @ApiModelProperty("患者所属区域")
    @TableField(exist = false)
    private Area area;

}
