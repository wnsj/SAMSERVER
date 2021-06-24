package com.jiubo.sam.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-06-16
 */
@Data
public class PayServiceDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "个人收费项目ID")
    private Integer ppId;

    @ApiModelProperty(value = "收费项目ID")
    private Integer payserviceId;

    @ApiModelProperty(value = "是否启用(0:关闭;1:开启;2:取消;3:区间计费)")
    private Integer isUse;

    @ApiModelProperty(value = "启用时间")
    private Date begDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "患者住院号")
    private String hospNum;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "预收")
    private BigDecimal preReceive;

    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "创建人")
    private Integer creator;

    @ApiModelProperty(value = "修改人")
    private Integer reviser;


    @ApiModelProperty(value = "部门id")
    private Integer deptId;


}
