package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.LogRecordsBean;
import com.jiubo.sam.bean.ProjectCostManageBean;
import com.jiubo.sam.dao.ProjectCostManageDao;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.ProjectCostManageService;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectCostManageImpl extends ServiceImpl<ProjectCostManageDao, ProjectCostManageBean> implements ProjectCostManageService {
    @Autowired
    private ProjectCostManageDao projectCostManageDao;

    @Autowired
    private LogRecordsService logRecordsService;

    @Override
    public List<ProjectCostManageBean> queryProjectList(ProjectCostManageBean projectCostManageBean) {

        return projectCostManageDao.queryProjectList(projectCostManageBean);
    }

    @Override
    public void updateProjectBilling(ProjectCostManageBean projectCostManageBean) throws Exception {
        int i = projectCostManageDao.updateProjectBilling(projectCostManageBean);
        //添加日志
        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(projectCostManageBean.getHospNum())
                .setOperateId(Integer.valueOf(projectCostManageBean.getAccount()))
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule("启动项目管理，修改项目计费时间和单价")
                .setOperateType("修改")
                .setLrComment(projectCostManageBean.toString())
        );
        System.out.println("影响"+i+"行");
    }
}
