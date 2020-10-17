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
    private Integer operateId;

    /**
     * 操作时间
     */
    private LocalDateTime createDate;

    /**
     * 操作患者
     */
    private String patientId;

    /**
     * 操作内容
     */
    private String lrComment;


}
