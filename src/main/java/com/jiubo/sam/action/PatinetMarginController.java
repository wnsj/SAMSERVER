package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PatinetMarginBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PatinetMarginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
@Api(tags = "押金接口")
@RestController
@RequestMapping("/patinetMargin")
public class PatinetMarginController {

    @Autowired
    private PatinetMarginService patinetMarginService;

    @ApiOperation(value = "添加或退押金")
    @PostMapping("/addPatinetMargin")
    public JSONObject addPatinetMargin(@RequestBody PatinetMarginBean patinetMarginBean) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        patinetMarginService.addPatinetMargin(patinetMarginBean);
        return jsonObject;
    }

}
