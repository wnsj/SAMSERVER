package com.jiubo.sam.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentOneCondition {
    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "患者名字")
    private String name;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "科室")
    private Integer deptId;

    @ApiModelProperty(value = "维护医生")
    private Integer empId;

    @ApiModelProperty(value = "截止日期开始时间")
    private Date endBegDate;

    @ApiModelProperty(value = "截止日期结束时间")
    private Date endEndDate;

    @ApiModelProperty(value = "缴费开始时间")
    private Date begDate;

    @ApiModelProperty(value = "缴费结束时间")
    private Date endDate;

    @ApiModelProperty(value = "在院状态【1：在院；0：出院】")
    private String inHosp;

    @ApiModelProperty(value = "医保类型")
    private Integer mitypeid;

    @ApiModelProperty(value = "患者类型")
    private Integer patitypeid;

    @ApiModelProperty(value = "性别【1：男；2：女】")
    private Integer sex;

    @ApiModelProperty(value = "缴退类型【0：缴费；1：退费】")
    private String payment_status;

    @ApiModelProperty(value = "启动项目id")
    private Integer serviceId;

    @ApiModelProperty(value = "缴费开始时间")
    private Date serviceStartDate;

    @ApiModelProperty(value = "缴费结束时间")
    private Date serviceEndDate;
}
