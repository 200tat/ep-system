package com.primeton.commom.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("方舱")
public class Shelters implements Serializable {
    @ApiModelProperty("方舱id")
    @TableId
    private String sheltersId;

    @ApiModelProperty("方舱名称")
    private String sheltersName;

    @ApiModelProperty("方舱联系电话")
    private String sheltersPhone;

    @ApiModelProperty("方舱所在地")
    private String sheltersAddress;

    @ApiModelProperty("方舱状态 0关闭1开启")
    private Integer sheltersStatus;
}
