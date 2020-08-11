package com.jiubo.sam.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.SysAccountBean;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mwl
 * @since 2020-02-10
 */
public interface SysAccountService {

    //查询账号
    public List<SysAccountBean> queryAccountList(SysAccountBean accountBean) throws Exception;

    public JSONObject login(String param) throws Exception;


    //添加账号
    public SysAccountBean addAccount(SysAccountBean accountBean) throws Exception;

    //修改账号
    public void patchAccount(SysAccountBean accountBean) throws Exception;

    public Page<SysAccountBean> queryAccountByPage(SysAccountBean accountBean);

    public void deleteAccById(SysAccountBean accountBean);
}
