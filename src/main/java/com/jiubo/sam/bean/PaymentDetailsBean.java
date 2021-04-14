package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 缴费明细
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PAYMENT_DETAILS")
@ApiModel(value="PaymentDetailsBean对象", description="缴费明细")
public class PaymentDetailsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "缴费明细ID")
    @TableId(value = "PD_ID", type = IdType.AUTO)
    private Integer pdId;

    @ApiModelProperty(value = "住院1门诊2押金3")
    private Integer type;

    @ApiModelProperty(value = "1添加2退费")
    private Integer marginType;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime begDate;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endDate;

    @ApiModelProperty(value = "部门ID")
    private Integer deptId;

    @ApiModelProperty(value = "是否在院(1在院2不在")
    private Integer isInHospital;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "维护医生")
    private Integer empId;

    @ApiModelProperty(value = "押金发生")
    private Double marginUse;

    @ApiModelProperty(value = "门诊发生")
    private Double patientUse;

    @ApiModelProperty(value = "住院发生")
    private Double hospitalUse;

    @ApiModelProperty(value = "当前此人剩余的余额")
    private Double currentMargin;

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

    @ApiModelProperty(value = "押金")
    @TableField(exist = false)
    private String marginAmount;

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
