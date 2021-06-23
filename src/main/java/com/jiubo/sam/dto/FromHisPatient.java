package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FromHisPatient {

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ApiModelProperty(value = "年龄")
    private String age;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "患者联系电话")
    private String patientPhone;

    @ApiModelProperty(value = "科室id")
    private Integer deptId;

    @ApiModelProperty(value = "入院时间")
    private Date hospTime;

    @ApiModelProperty(value = "出院时间")
    private Date outHosp;

    @ApiModelProperty(value = "住院余额")
    private BigDecimal hospBalance;
}
