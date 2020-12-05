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
@TableName("PAYMENT")
public class ProjectCostManageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //类型Id
    @TableId(value = "PAYMENT_ID", type = IdType.AUTO)
    private String paymentId;

    private String account;

    private String patientId;

    @TableField(exist = false)
    private String hospNum;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String deptId;

    @TableField(exist = false)
    private String hospTime;

    private String begTime;

    private String payserviceId;

    private String paymentTime;

    private String price;

    @TableField(exist = false)
    private String deptName;

    @TableField(exist = false)
    private String projectName;
}
