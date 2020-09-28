package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @author mwl
 * @since 2020-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_account")
public class SysAccountBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "SA_ID", type = IdType.AUTO)
    private Integer saId;


    private String name;

    private String phone;

    private String account;

    private String pwd;

    private Date createTime;
    @TableField(exist = false)
    private List<RoleBean> roleBeanList;

    @TableField(exist = false)
    private List<Integer> roleIdList;

    private String roleName;

    /**
     * 页码
     */
    @TableField(exist = false)
    private String current;

    /**
     * 每页尺寸
     */
    @TableField(exist = false)
    private String pageSize;

    private Integer flag;

    private Integer roleId;
    private Integer empId;

    private String expireDate;
}
