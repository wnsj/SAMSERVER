package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.bean.ProjectCostManageBean;

import java.util.List;

public interface ProjectCostManageDao extends BaseMapper<ProjectCostManageBean> {

    List<ProjectCostManageBean> queryProjectList(ProjectCostManageBean projectCostManageBean);

    int updateProjectBilling(ProjectCostManageBean projectCostManageBean);
}
