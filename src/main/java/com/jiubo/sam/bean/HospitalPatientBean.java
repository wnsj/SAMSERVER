package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 住院门诊
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("HOSPITAL_PATIENT")
@ApiModel(value="HospitalPatientBean对象", description="住院门诊")
public class HospitalPatientBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "住院门诊ID")
    @TableId(value = "HP_ID", type = IdType.AUTO)
    private Integer hpId;

    @ApiModelProperty(value = "住院1门诊2")
    private Integer type;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime begDate;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endDate;

    @ApiModelProperty(value = "天数")
    private Integer days;

    @ApiModelProperty(value = "操作人")
    private Integer accountId;

    @ApiModelProperty(value = "部门ID")
    private Integer deptId;

    @ApiModelProperty(value = "是否在院(1在院2不在")
    private Integer isInHospital;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "维护医生")
    private Integer empId;

    @ApiModelProperty(value = "实发生金额")
    private Double realCross;

    @ApiModelProperty(value = "申报金额")
    private Double amountDeclared;

    @ApiModelProperty(value = "自费金额")
    private Double amount;

    @ApiModelProperty(value = "执行科室")
    private Integer executeDept;

    @ApiModelProperty(value = "门诊断")
    private String textPat;

    @ApiModelProperty(value = "缴费1退费2")
    private Integer consumType;

    @ApiModelProperty(value = "姓名")
    @TableField(exist = false)
    private String patientName;

    @ApiModelProperty(value = "科室名称")
    @TableField(exist = false)
    private String deptName;

    @ApiModelProperty(value = "维护医生名称")
    @TableField(exist = false)
    private String empName;

    @ApiModelProperty(value = "患者类型名称")
    @TableField(exist = false)
    private String patiTypeName;

    @ApiModelProperty(value = "医保类型名称")
    @TableField(exist = false)
    private String miTypeName;

    @ApiModelProperty(value = "执行科室名称")
    @TableField(exist = false)
    private String execDeptName;

    @ApiModelProperty(value = "预交金")
    @TableField(exist = false)
    private Double marginAmount;

    @ApiModelProperty(value = "性别")
    @TableField(exist = false)
    private Integer sex;

    @ApiModelProperty(value = "年龄")
    @TableField(exist = false)
    private Integer age;

    @ApiModelProperty(value = "是否欠费(1欠费2不欠费")
    @TableField(exist = false)
    private Integer isArrearage;


}
