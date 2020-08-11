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
 * @author syl
 * @since 2020-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("menu")
public class MenuBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单名
     */
    private String menuName;

    /**
     * 菜单唯一标识
     */
    private String menuCode;

    /**
     * 菜单路径
     */
    private String menuPath;

    /**
     * 父菜单id
     */
    private int parentId;

    private String parentName;
    /**
     * 级别
     */
    private Integer level;

    /**
     * 状态（0禁用；1启用）
     */
    private Integer state;

    private String stateLabel;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * id集合
     */
    @TableField(exist = false)
    private List<Integer> idList;
    @TableField(exist = false)
    private List<MenuBean> menuBeanList;
    @TableField(exist = false)
    private Integer roleId;

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

    private Integer type;

    private String ip;
    @TableField(exist = false)
    private String user;
}
