package com.jiubo.sam.service.impl;

import com.alibaba.fastjson.JSON;
import com.jiubo.sam.bean.AccountBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.dao.AccountDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.util.CookieTools;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-20
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, AccountBean> implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public List<AccountBean> queryAccount(AccountBean accountBean) {
        return accountDao.queryAccount(accountBean);
    }

    @Override
    public AccountBean login(AccountBean accountBean) throws Exception {
        if (StringUtils.isBlank(accountBean.getAccountNum()))throw new MessageException("账号不能为空!");
        if(StringUtils.isBlank(accountBean.getAccountPwd()))throw new MessageException("密码不能为空!");
        List<AccountBean> accountBeans = queryAccount(accountBean);
        AccountBean bean = null;
        if(accountBeans.size() <= 0){
            throw new MessageException("账号名或命名错误!");
        }else{
            bean = accountBeans.get(0);
            bean.setAccountPwd("");
            if("0".equals(bean.getAccountState()) || "false".equals(bean.getAccountState()))throw new MessageException("该账号已被停用，请联系管理员!");
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            CookieTools.addCookie(response,"accessToken", URLEncoder.encode(bean.getAccountNum().concat("604800"), Constant.Charset.UTF8), 604800);
            CookieTools.addCookie(response,"accountData", URLEncoder.encode(JSON.toJSONString(bean), Constant.Charset.UTF8), 604800);
        }
        return bean;
    }

    @Override
    public void updateAccount(AccountBean accountBean) throws Exception {
        if (StringUtils.isBlank(accountBean.getAccountNum()))throw new MessageException("账号不能为空!");
        if(StringUtils.isBlank(accountBean.getAccountPwd()))throw new MessageException("密码不能为空!");
        if(accountDao.updateAccount(accountBean) <= 0)throw new MessageException("操作失败!");
    }
}
