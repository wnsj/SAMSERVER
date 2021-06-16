package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.ProjectCostManageBean;
import com.jiubo.sam.dto.ClosedPro;
import com.jiubo.sam.dto.OpenPro;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface ProjectCostManageService extends IService<ProjectCostManageBean> {

    Page<ProjectCostManageBean> queryProjectList(ProjectCostManageBean projectCostManageBean) throws Exception;

    void updateProjectBilling(ProjectCostManageBean projectCostManageBean) throws Exception;

    List<ClosedPro> getClosedProByPID(Integer id);

    List<OpenPro> getOpenProByPID(Integer id);
}
