package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.ProjectCostManageBean;

import java.util.List;

public interface ProjectCostManageService extends IService<ProjectCostManageBean> {

    List<ProjectCostManageBean> queryProjectList(ProjectCostManageBean projectCostManageBean)throws Exception;

    void updateProjectBilling(ProjectCostManageBean projectCostManageBean)throws Exception;
}
