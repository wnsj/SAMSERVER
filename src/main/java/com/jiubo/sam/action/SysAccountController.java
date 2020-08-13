package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.SysAccountBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.SysAccountService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mwl
 * @since 2020-02-10
 */

@RestController
@RequestMapping("/SysAccountController")
public class SysAccountController {

    @Autowired
    private SysAccountService accountService;


    //用户登录
    @PostMapping("/login")
    public JSONObject login(@RequestBody String params) throws Exception {
        System.out.println("入参{}"+params);
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);

//        System.out.println("登录数据：" + ip);
//        String publicIp = IPUtil.getPublicIp();
        jsonObject.put(Constant.Result.RETDATA, accountService.login(params));
        return jsonObject;
    }

    //微信账号添加
    @PostMapping("/addAccount")
    public JSONObject addAccount(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        System.out.println("测试：" + params.toString());

        SysAccountBean accountBean = JSONObject.parseObject(params, SysAccountBean.class);
        jsonObject.put(Constant.Result.RETDATA, accountService.addAccount(accountBean));
        return jsonObject;
    }


    @PostMapping("/patchAccount")
    public JSONObject patchAccount(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        SysAccountBean accountBean = JSONObject.parseObject(params, SysAccountBean.class);
        accountService.patchAccount(accountBean);
        return jsonObject;
    }

    @PostMapping("/getAccountByPage")
    public JSONObject queryAccountByPage(@RequestBody SysAccountBean accountBean) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA, accountService.queryAccountByPage(accountBean));
        return jsonObject;
    }
    @PostMapping("/deleteAcc")
    public JSONObject deleteAccById(@RequestBody SysAccountBean accountBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        accountService.deleteAccById(accountBean);
        return jsonObject;
    }
}
