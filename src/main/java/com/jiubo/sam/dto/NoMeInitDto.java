package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class NoMeInitDto {
    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "患者姓名")
    private String name;

    @ApiModelProperty(value = "应收")
    private BigDecimal receivableCount;

    @ApiModelProperty(value = "实收")
    private BigDecimal netReceiptsCount;

    @ApiModelProperty(value = "科室名")
    private String deptName;

    @ApiModelProperty(value = "是否在院【1:在院，0:出院】")
    private Integer isHp;

    @ApiModelProperty(value = "入院时间")
    private Date hpDate;

    @ApiModelProperty(value = "维护医生")
    private String empName;

    @ApiModelProperty(value = "患者类型")
    private String paType;

    @ApiModelProperty(value = "医保类型")
    private String miType;

    @ApiModelProperty(value = "出院时间")
    private Date outHpDate;

    @ApiModelProperty(value = "启动项目数据")
    private List<NoMeProDto> proDtoList;
}
