package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 患者基础信息Bean
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PATIENT")
public class PatientBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //患者id
    @TableId(value = "PATIENT_ID", type = IdType.AUTO)
    private String patientId;

    //住院号
    private String hospNum;

    //姓名
    private String name;

    //性别
    private String sex;

    //年龄
    private String age;

    //住院日期
    private String hospTime;

    //是否在院【1:在院，0:全部，2：出院】
    private String inHosp;

    //出院时间
    private String outHosp;

    //科室id
    private String deptId;

    //每月应收
    private String receivable;

    //修改时间
    private String updateTime;

    //交费信息
    private List<PaymentBean> paymentList;
}
