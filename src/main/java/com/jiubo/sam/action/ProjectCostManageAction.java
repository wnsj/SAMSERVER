package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.MenuBean;
import com.jiubo.sam.bean.ProjectCostManageBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.dto.ClosedPro;
import com.jiubo.sam.dto.ClosedProListDto;
import com.jiubo.sam.dto.UpdateProDto;
import com.jiubo.sam.dto.OpenPro;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.ProjectCostManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 缴费管理
 * 启动项目管理
 * </p>
 *
 * @author mwl
 * @since 2020-12-04
 */
@Api(tags = "启动项目管理")
@RestController
@Scope("prototype")
@RequestMapping("/ProjectCostManage")
public class ProjectCostManageAction {
    @Autowired
    private ProjectCostManageService projectCostManageService;

    //查询项目费用详情
    @PostMapping("/queryProjectList")
    public JSONObject queryProjectList(@RequestBody ProjectCostManageBean projectCostManageBean) throws Exception {
//        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
//        ProjectCostManageBean projectCostManageBean = JSONObject.parseObject(params, ProjectCostManageBean.class);
        jsonObject.put(Constant.Result.RETDATA, projectCostManageService.queryProjectList(projectCostManageBean));
        return jsonObject;
    }

    //修改医疗费
    @PostMapping("/updateProjectBilling")
    public JSONObject updateProjectBilling(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        ProjectCostManageBean projectCostManageBean = JSONObject.parseObject(params, ProjectCostManageBean.class);
        projectCostManageService.updateProjectBilling(projectCostManageBean);
        return jsonObject;
    }

    @ApiOperation(value = "根据患者id查询关闭项目")
    @GetMapping("/getClosedProByPID")
    public List<ClosedPro> getClosedProByPID(@RequestParam(value = "id") Integer id) throws MessageException {
        return projectCostManageService.getClosedProByPID(id);
    }

    @ApiOperation(value = "根据患者id查询开启项目")
    @GetMapping("/getOpenProByPID")
    public List<OpenPro> getOpenProByPID(@RequestParam(value = "id") Integer id) {
        return projectCostManageService.getOpenProByPID(id);
    }

    @ApiOperation(value = "关闭项目")
    @PostMapping("/closedPro")
    public JSONObject closedPro(@RequestBody ClosedProListDto closedProListDto) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        projectCostManageService.closedPro(closedProListDto);
        return jsonObject;
    }

    @ApiOperation(value = "修改项目")
    @PostMapping("/updatePro")
    public JSONObject updatePro(@RequestBody UpdateProDto updateProDto) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        projectCostManageService.updatePro(updateProDto);
        return jsonObject;
    }
}
