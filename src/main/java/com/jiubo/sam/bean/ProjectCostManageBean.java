package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private String begBillingTime;

    @TableField(exist = false)
    private String endBillingTime ;

    @TableField(exist = false)
    private String  begHospTime;

    @TableField(exist = false)
    private String  endHospTime;

    //单价
    private String unitPrice;

    @TableField(exist = false)
    private String pageSize;

    @TableField(exist = false)
    private String page;


}
