package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.AccountBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.AccountService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desc:用户接口
 * @date: 2019-09-20 08:55
 * @author: dx
 * @version: 1.0
 */
@RestController
@Scope("prototype")
@RequestMapping("/accountAction")
public class AccountAction {

    @Autowired
    private AccountService accountService;

    //用户登录
    @PostMapping("/login")
    public JSONObject login(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        AccountBean accountBean = JSONObject.parseObject(params, AccountBean.class);
        jsonObject.put(Constant.Result.RETDATA, accountService.login(accountBean));
        return jsonObject;
    }

    //修改用户信息
    @PostMapping("/updateAccount")
    public JSONObject updateAccount(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        AccountBean accountBean = JSONObject.parseObject(params, AccountBean.class);
        accountService.updateAccount(accountBean);
        return jsonObject;
    }
}
