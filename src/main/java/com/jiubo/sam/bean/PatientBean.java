package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

    @TableField(exist = false)
    private List<String> deptList;

    //科室名
    @TableField(exist = false)
    private String deptName;

    //每月应收
    private String receivable;

    //修改时间
    private String updateTime;

    //维护人
    private String empId;

    //交费信息
    private List<PaymentBean> paymentList;

    //患者类型Id
    private String patitypeid;

    //患者类型名
    private String patitypename;

    //医保类型Id
    private String mitypeid;

    //医保类型名
    private String mitypename;

    //操作人
    private String accountId;

    //操作账号
    @TableField(exist = false)
    private String accountName;

    //时差
    private String dateDiffrent;

    //最近缴费结束时间
    private String endDate;

    @TableField(exist = false)
    private String begHospTime;

    @TableField(exist = false)
    private String endHospTime;

    @TableField(exist = false)
    private String payServiceArr[];

    //收费项目是否启动
    @TableField(exist = false)
    private Integer isStart;

    @TableField(exist = false)
    private String page;

    @TableField(exist = false)
    private String pageSize;

    @TableField(exist = false)
    private String empName;

    //是否合并数据【1：是】
    @TableField(exist = false)
    private String isMerge;

    //非医疗费汇总
    @TableField(exist = false)
    private String medicalTatol;

    //医疗费汇总
    @TableField(exist = false)
    private String paymentArrears;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientBean that = (PatientBean) o;
        return Objects.equals(hospNum, that.hospNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospNum);
    }
}
