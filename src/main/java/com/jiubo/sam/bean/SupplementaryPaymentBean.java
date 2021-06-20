package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 医疗费补缴记录Bean
 * </p>
 *
 * @author dx
 * @since 2020-08-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("SUPPLEMENTARY_PAYMENT")
public class SupplementaryPaymentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 补缴记录ID
     */
    @TableId(value = "SP_ID", type = IdType.AUTO)
    private Integer spId;

    /**
     * 医疗费ID
     */
    @ApiModelProperty(value = "医疗费ID")
    private Integer meId;

    /**
     * 补缴金额
     */
    @ApiModelProperty(value = "补缴金额")
    private BigDecimal money;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private Integer sort;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String startDate;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String endDate;

}
