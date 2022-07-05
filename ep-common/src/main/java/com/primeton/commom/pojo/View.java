package com.primeton.commom.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel("疫情大数据报告")
public class View implements Serializable {
    @ApiModelProperty("主键ID")
    @TableId
    private String viewId;

    @ApiModelProperty("区域主键")
    private String areaId;

    @ApiModelProperty("区域名称")
    private String areaName;

    @ApiModelProperty("父区域id")
    private String parentId;

    @ApiModelProperty("父级区域名称")
    private String parentName;

    @ApiModelProperty("区域等级 0上海1行政区2街道3小区")
    private Integer areaLevel;

    @ApiModelProperty("新增确诊")
    private Integer newPatientCount;

    @ApiModelProperty("新增死亡")
    private Integer newDeathCount;

    @ApiModelProperty("新增治愈")
    private Integer newCuredCount;

    @ApiModelProperty("现有确诊")
    private Integer presentPatientCount;

    @ApiModelProperty("累计确诊")
    private Integer allPatientCount;

    @ApiModelProperty("累计治愈")
    private Integer allCuredCount;

    @ApiModelProperty("累计死亡")
    private Integer allDeathCount;

    @ApiModelProperty("封控楼栋")
    private String building;

    @ApiModelProperty("三区划分数量")
    private Integer isolationLevel;

    @ApiModelProperty("转到方舱人数")
    private Integer transferCount;

    @ApiModelProperty("转到防疫中心人数")
    private Integer stayCount;

    @ApiModelProperty("当日日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date statisticalDate;

    @ApiModelProperty("今日转运人数")
    private Integer transferCountToday;


    @ApiModelProperty("区域直接下级子区域")
    @TableField(exist = false)
    //boolean outstanding; true(上榜) false(所有)
    private List<View> children; // 包含此区域下的所有子代区域  浦东区 {周浦街道{美林：{。。。}，蓝田居：{。。。}，川沙：{街道1：{} }} }

}
