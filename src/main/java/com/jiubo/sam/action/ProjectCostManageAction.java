package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.ProjectCostManageBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.ProjectCostManageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 缴费管理
 * 启动项目管理
 * </p>
 *
 * @author mwl
 * @since 2020-12-04
 */
@RestController
@Scope("prototype")
@RequestMapping("/ProjectCostManage")
public class ProjectCostManageAction {
    @Autowired
    private ProjectCostManageService projectCostManageService;

    //查询项目费用详情
    @PostMapping("/queryProjectList")
    public JSONObject queryProjectList(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        ProjectCostManageBean projectCostManageBean = JSONObject.parseObject(params, ProjectCostManageBean.class);
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
}
