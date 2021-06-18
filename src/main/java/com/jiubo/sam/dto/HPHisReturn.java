package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HPHisReturn {

    @ApiModelProperty(value = "sam流水号")
    private String samLowNum;

    @ApiModelProperty(value = "his流水号")
    private String hisLowNum;
}
