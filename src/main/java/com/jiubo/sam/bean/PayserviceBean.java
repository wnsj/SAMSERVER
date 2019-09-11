package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 收费项目Bean
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PAYSERVICE")
public class PayserviceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //收费项目id
    @TableId(value = "PAYSERVICE_ID", type = IdType.AUTO)
    private String payserviceId;

    //收费项目名
    private String name;

    //是否启用【1:启用， 0:不启用】
    private boolean isuse;


}
