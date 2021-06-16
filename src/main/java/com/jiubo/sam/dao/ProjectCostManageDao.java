package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.bean.ProjectCostManageBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectCostManageDao extends BaseMapper<ProjectCostManageBean> {

    List<ProjectCostManageBean> queryProjectList(@Param("page") Page page, @Param("projectCostManageBean") ProjectCostManageBean projectCostManageBean);

    int updateProjectBilling(ProjectCostManageBean projectCostManageBean);

    List<PaPayserviceBean> getToRemovePro(@Param("pId") Integer pId);

    List<PayserviceBean> getAllPayService();

    PaPayserviceBean getNextCloseDate(@Param("patientId") Integer patientId,@Param("payServiceId") Integer payServiceId);
}
