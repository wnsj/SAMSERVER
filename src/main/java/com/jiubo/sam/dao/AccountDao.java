package com.jiubo.sam.dao;

import com.jiubo.sam.bean.AccountBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-20
 */
public interface AccountDao extends BaseMapper<AccountBean> {

    //查询用户
    public List<AccountBean> queryAccount(AccountBean accountBean);

    //修改用户信息
    public int updateAccount(AccountBean accountBean);
}
