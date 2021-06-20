package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author dx
 * @since 2021-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("no_medical")
@ApiModel(value="NoMedicalBean对象", description="")
public class NoMedicalBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "患者身份证号")
    private String idCard;

    @ApiModelProperty(value = "缴费日期")
    private Date payDate;

    @ApiModelProperty(value = "缴费日期【年月日格式化】")
    private String payDateFormat;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal noMedicalMoney;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "姓名")
    private String paName;

    @ApiModelProperty(value = "科室")
    private String deptName;

    @ApiModelProperty(value = "在院状态(1:在院;2:出院)")
    private Integer isHosp;

    @ApiModelProperty(value = "维护医生")
    private String doctor;

    @ApiModelProperty(value = "余额")
    private BigDecimal balance;

    @ApiModelProperty("科室id")
    private Integer deptId;

    @ApiModelProperty(value = "开始日期")
    private String begDate;

    @ApiModelProperty(value = "开始日期")
    private String endDate;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;
}
