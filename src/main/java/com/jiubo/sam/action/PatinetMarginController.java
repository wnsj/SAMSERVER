package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PatinetMarginBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.dto.RemarkDto;
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


    @ApiOperation(value = "补录添加或退押金")
    @PostMapping("/suMargin")
    public String suMargin(@RequestBody PatinetMarginBean patinetMarginBean) throws Exception{
        return patinetMarginService.suMargin(patinetMarginBean);
    }

    @ApiOperation(value = "修改预交金备注")
    @PostMapping("/updateMarginRemark")
    public int updateMarginRemark(@RequestBody RemarkDto remarkDto) throws Exception{
       return patinetMarginService.updateMarginRemark(remarkDto);
    }

    @ApiOperation(value = "修改医疗费备注")
    @PostMapping("/updateMeRemark")
    public int updateMeRemark(@RequestBody RemarkDto remarkDto) throws Exception{
        return patinetMarginService.updateMeRemark(remarkDto);
    }
}
