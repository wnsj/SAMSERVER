package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.ProjectCostManageBean;
import com.jiubo.sam.dto.ClosedPro;
import com.jiubo.sam.dto.OpenPro;
import com.jiubo.sam.dto.ClosedProListDto;
import com.jiubo.sam.dto.UpdateProDto;
import com.jiubo.sam.exception.MessageException;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ProjectCostManageService extends IService<ProjectCostManageBean> {

    Page<ProjectCostManageBean> queryProjectList(ProjectCostManageBean projectCostManageBean) throws Exception;

    void updateProjectBilling(ProjectCostManageBean projectCostManageBean) throws Exception;

    List<ClosedPro> getClosedProByPID(Integer id) throws MessageException;

    List<OpenPro> getOpenProByPID(Integer id);

    void closedPro(ClosedProListDto closedProListDto) throws ParseException, Exception;

    void updatePro(UpdateProDto updateProDto) throws ParseException, Exception;
}
