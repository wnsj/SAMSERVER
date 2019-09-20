package com.jiubo.sam.service;

import com.jiubo.sam.bean.AccountBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.exception.MessageException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-20
 */
public interface AccountService extends IService<AccountBean> {

    //查询用户
    public List<AccountBean> queryAccount(AccountBean accountBean);

    //用户登录
    public AccountBean login(AccountBean accountBean) throws Exception;

    //修改用户信息
    public void updateAccount(AccountBean accountBean)throws Exception;
}
