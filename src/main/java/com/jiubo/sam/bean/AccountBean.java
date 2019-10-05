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
 * 用户Bean
 * </p>
 *
 * @author dx
 * @since 2019-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("Account")
public class AccountBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户Id
    @TableId(value = "ACCOUNT_ID", type = IdType.AUTO)
    private String accountId;

    //账号
    private String accountNum;

    //密码
    private String accountPwd;

    //用户名
    private String accountName;

    //状态【1：启用，0：不启用】
    private String accountState;

    //账户类型【1：有查询权限，2：增删改权限】
    private String accountType;
}
