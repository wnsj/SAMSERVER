package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NoMeDto {

    @ApiModelProperty(value = "时间")
    private String dateTime;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "开始时间")
    private String begDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "服务名称")
    private String serviceName;

}
