package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.request.PrintRequest;
import com.jiubo.sam.service.PrintsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @since 2021-04-15
 */
@Api(tags = "打印相关代码")
@RestController
@RequestMapping("/printBean")
public class PrintController {

    @Autowired
    private PrintsService printService;

    @PostMapping("/addPrint")
    @ApiOperation("添加打印记录")
    public JSONObject addPrint(@RequestBody PrintRequest printRequest){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA, printService.addPrint(printRequest));
        return jsonObject;
    }
}
