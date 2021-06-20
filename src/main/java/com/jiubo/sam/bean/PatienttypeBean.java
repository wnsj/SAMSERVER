package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 患者类型Bean
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PATIENTTYPE")
public class PatienttypeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //类型Id
    @TableId(value = "PATITYPEID", type = IdType.AUTO)
    private String patitypeid;

    //类型名
    @ApiModelProperty(value = "类型名")
    private String patitypename;

    //备注
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 1:在用
     * 0:不在用
     */
    @ApiModelProperty(value = " 1:在用 0:不在用")
    private String isuse;


}
