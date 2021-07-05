package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class NoMeProDto {

    @ApiModelProperty(value = "启动项目名")
    private String proName;

    @ApiModelProperty(value = "启动项目id",required = true,hidden = true)
    private Integer proId;

    @ApiModelProperty(value = "实收汇总")
    private BigDecimal netReceiptsTotal;

    @ApiModelProperty(value = "费用明细数据")
    private List<NoMeFeeDto> noMeFeeDtoList;
}
