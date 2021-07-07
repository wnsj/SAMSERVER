package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientHiSDto {

    private Integer id;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "身份证号")
    private String identityCard;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "年龄")
    private String age;

    @ApiModelProperty(value = "科室id")
    private String deptId;

    @ApiModelProperty(value = "医保类型")
    private String mitypeid;

    @ApiModelProperty(value = "创建人")
    private Integer creator;

    @ApiModelProperty(value = "his流水")
    private String hisWaterNum;

    @ApiModelProperty(value = "入院时间")
    private Date hospDate;
}
