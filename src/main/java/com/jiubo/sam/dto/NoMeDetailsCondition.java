package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class NoMeDetailsCondition {

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "患者姓名")
    private String name;

    @ApiModelProperty(value = "启动项计费开始时间")
    private Date chargeStartDate;

    @ApiModelProperty(value = "启动项计费结束时间")
    private Date chargeEndDate;

    @ApiModelProperty(value = "入院开始时间")
    private Date hpStartDate;

    @ApiModelProperty(value = "入院结束时间")
    private Date hpEndDate;

    @ApiModelProperty(value = "出院开始时间")
    private Date outHpStartDate;

    @ApiModelProperty(value = "出院结束时间")
    private Date outHpEndDate;

    @ApiModelProperty(value = "科室")
    private Integer dept;

    @ApiModelProperty(value = "医保类型Id")
    private Integer miTypeId;

    @ApiModelProperty(value = "患者类型Id")
    private Integer paTypeId;

    @ApiModelProperty(value = "维护医生")
    private Integer empId;

    @ApiModelProperty(value = "是否在院【1:在院，0:出院】")
    private String inHosp;
}
