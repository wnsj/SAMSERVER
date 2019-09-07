package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
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

    @TableId(value = "PATIENT_ID", type = IdType.AUTO)
    private Integer patientId;

    private String hospNum;

    private String name;

    private Integer sex;

    private Integer age;

    private LocalDateTime hospTime;

    /**
     * 0:在院
   1:出院
     */
    private Integer inHosp;

    private LocalDateTime outHosp;

    private Integer deptId;

    private Double receivable;

    private LocalDateTime updateTime;


}
