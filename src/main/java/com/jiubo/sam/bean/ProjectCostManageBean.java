package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 医保类型Bean
 * </p>
 *
 * @author mwl
 * @since 2020-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PA_PAYSERVICE")
public class ProjectCostManageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //类型Id
    @TableId(value = "PAYSERVICE_ID", type = IdType.AUTO)
    private String payserviceId;

    @TableField(exist = false)
    private String account;

    private String patientId;

    private String hospNum;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String deptId;

    @TableField(exist = false)
    private String deptName;

    @TableField(exist = false)
    private String projectName;

    @TableField(exist = false)
    private String hospTime;

    private String begDate;

    @TableField(exist = false)
    private Date begBillingTime;

    @TableField(exist = false)
    private Date endBillingTime ;

    @TableField(exist = false)
    private Date begHospTime;

    @TableField(exist = false)
    private Date  endHospTime;

    //单价
    @ApiModelProperty(value = "单价")
    private String unitPrice;

    @TableField(exist = false)
    private String pageSize;

    @TableField(exist = false)
    private String page;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "计费状态【0:关闭;1:开启;2:取消;3:区间计费】")
    private Integer isUse;

    @ApiModelProperty(value = "收费类型【0:默认计费;1:区间计费】")
    @TableField(exist = false)
    private Integer payType;

    @ApiModelProperty(value = "项目")
    @TableField(exist = false)
    private List<Integer> proIdList;

    @ApiModelProperty(value = "计费日期类型【1:精确;2:包含】")
    @TableField(exist = false)
    private Integer dateType;

    @ApiModelProperty(value = "在院状态【0:在院;1:出院】")
    @TableField(exist = false)
    private Integer inHosp;

    @ApiModelProperty(value = "修改时间")
    private Date updateDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "维护人")
    @TableField(exist = false)
    private String empName;

}
