package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.ProjectCostManageBean;

import java.util.Map;

public interface ProjectCostManageService extends IService<ProjectCostManageBean> {

    Page<ProjectCostManageBean> queryProjectList(ProjectCostManageBean projectCostManageBean)throws Exception;

    void updateProjectBilling(ProjectCostManageBean projectCostManageBean)throws Exception;
}
