package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 日志记录表
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("LOG_RECORDS")
public class LogRecordsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "LR_ID", type = IdType.AUTO)
    private Integer lrId;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Integer operateId;

    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    private String createDate;

    /**
     * 操作患者
     */
    @ApiModelProperty(value = "操作患者")
    private String hospNum;

    /**
     * 操作模块
     */
    @ApiModelProperty(value = "操作模块")
    private String operateModule;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型")
    private String operateType;

    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容")
    private String lrComment;

    /**
     * 操作人姓名
     */
    @ApiModelProperty(value = "操作人姓名")
    private String name;


    @TableField(exist = false)
    private String page;

    @TableField(exist = false)
    private String pageSize;

    @TableField(exist = false)
    private String begDate;

    @TableField(exist = false)
    private String endDate;
}
