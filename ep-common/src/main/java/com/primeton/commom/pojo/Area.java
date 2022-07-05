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
@TableName(value = "area")
@ApiModel("区域")
public class Area implements Serializable {
    @TableId
    @ApiModelProperty("区域id")
    private String areaId;

    @ApiModelProperty("区域名称")
    private String areaName;

    @ApiModelProperty("区域联系电话")
    private String areaPhone;

    @ApiModelProperty("三区划分 1防控区2管控区3封控区")
    private Integer areaStatus;

    @ApiModelProperty("区域等级 1区2街道3小区")
    private Integer areaLevel;

    @ApiModelProperty("父级区域id")
    private String parentId;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("区域的直接上级区域")
    @TableField(exist = false)
    private Area parentArea;

    @ApiModelProperty("区域的直接子区域集合")
    @TableField(exist = false)
    private List<Area> children;
    @TableField(exist = false)
    private List<Patient> patientList;
    @TableField(exist = false)
    private List<PatientAreaShelters> pasList;


}
