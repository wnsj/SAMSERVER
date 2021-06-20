package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author dx
 * @since 2020-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("role")
public class RoleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Integer id;

    @TableField(exist = false)
    private List<Integer> idList;
    /**
     * 角色名
     */
    @ApiModelProperty(value = "角色名")
    private String roleName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    /**
     * 状态（2：禁用；1启用）
     */
    @ApiModelProperty(value = "状态（2：禁用；1启用）")
    private Integer state;

    /**
     * 状态描述
     */
    @ApiModelProperty(value = "状态描述")
    private String stateLabel;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码")
    @TableField(exist = false)
    private String current;

    /**
     * 每页尺寸
     */
    @ApiModelProperty(value = "每页尺寸")
    @TableField(exist = false)
    private String pageSize;

    private String ip;
    @TableField(exist = false)
    private String user;
}
