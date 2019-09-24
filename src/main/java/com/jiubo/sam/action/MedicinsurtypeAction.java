package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.MedicinsurtypeBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.MedicinsurtypeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 医保类型前端控制器
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
@RestController
@Scope("prototype")
@RequestMapping("/medicinsurtypeAction")
public class MedicinsurtypeAction {

    @Autowired
    private MedicinsurtypeService medicinsurtypeService;

    //查询医保类型
    @PostMapping("/queryMedicinsurtype")
    public JSONObject queryMedicinsurtype(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        MedicinsurtypeBean medicinsurtypeBean = JSONObject.parseObject(params, MedicinsurtypeBean.class);
        jsonObject.put(Constant.Result.RETDATA, medicinsurtypeService.queryMedicinsurtype(medicinsurtypeBean));
        return jsonObject;
    }

    //添加医保类型
    @PostMapping("/addMedicinsurtype")
    public JSONObject addMedicinsurtype(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        MedicinsurtypeBean medicinsurtypeBean = JSONObject.parseObject(params, MedicinsurtypeBean.class);
        medicinsurtypeService.addMedicinsurtype(medicinsurtypeBean);
        return jsonObject;
    }

    //修改医保类型
    @PostMapping("/updateMedicinsurtype")
    public JSONObject updateMedicinsurtype(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        MedicinsurtypeBean medicinsurtypeBean = JSONObject.parseObject(params, MedicinsurtypeBean.class);
        medicinsurtypeService.updateMedicinsurtype(medicinsurtypeBean);
        return jsonObject;
    }
}
