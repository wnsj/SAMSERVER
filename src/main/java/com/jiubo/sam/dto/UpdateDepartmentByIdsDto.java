package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateDepartmentByIdsDto {
    @ApiModelProperty(value = "部门id")
    private Integer deptId;

    @ApiModelProperty(value = "1开启，2关闭")
    private Integer isStrart;
}
