package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CACondition {

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "发生开始时间",required = true)
    private Date startDate;

    @ApiModelProperty(value = "发生结束时间",required = true)
    private Date endDate;

    @ApiModelProperty(value = "支付方式")
    private Integer payMethod;

    @ApiModelProperty(value = "支付类型")
    private Integer payType;

    @ApiModelProperty(value = "流水号")
    private String serialNumber;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;
}
