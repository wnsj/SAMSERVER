package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.LogRecordsBean;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.bean.ProjectCostManageBean;
import com.jiubo.sam.dao.PayserviceDao;
import com.jiubo.sam.dao.ProjectCostManageDao;
import com.jiubo.sam.dto.ClosedPro;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.ProjectCostManageService;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Wrapper;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectCostManageImpl extends ServiceImpl<ProjectCostManageDao, ProjectCostManageBean> implements ProjectCostManageService {
    @Autowired
    private ProjectCostManageDao projectCostManageDao;

    @Autowired
    private PayserviceDao payserviceDao;

    @Autowired
    private LogRecordsService logRecordsService;

    @Override
    public Page<ProjectCostManageBean> queryProjectList(ProjectCostManageBean projectCostManageBean) {
        Page<ProjectCostManageBean> page = new Page<>();
        page.setOptimizeCountSql(false);
        page.setCurrent(Long.valueOf(StringUtils.isBlank(projectCostManageBean.getPage()) ? "0" : projectCostManageBean.getPage()));
        page.setSize(Long.valueOf(StringUtils.isBlank(projectCostManageBean.getPageSize()) ? "10" : projectCostManageBean.getPageSize()));
        page.addOrder(new OrderItem().setAsc(true).setColumn("PATIENT_ID").setAsc(false).setColumn("BEG_DATE"));
        return page.setRecords(projectCostManageDao.queryProjectList(page,projectCostManageBean));

    }

    @Override
    public void updateProjectBilling(ProjectCostManageBean projectCostManageBean) throws Exception {
        int i = projectCostManageDao.updateProjectBilling(projectCostManageBean);
        //添加日志
        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(projectCostManageBean.getHospNum())
                .setOperateId(Integer.valueOf(projectCostManageBean.getAccount()))
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule("启动项目管理")
                .setOperateType("修改")
                .setLrComment(projectCostManageBean.toString())
        );
    }

    @Override
    public List<ClosedPro> getClosedProByPID(Integer id) {
        List<PaPayserviceBean> toRemovePro = projectCostManageDao.getToRemovePro(id);
        List<PayserviceBean> allPayService = projectCostManageDao.getAllPayService();
        if (!CollectionUtils.isEmpty(toRemovePro)) {
            List<String> list = toRemovePro.stream().map(PaPayserviceBean::getPayserviceId).distinct().collect(Collectors.toList());
            allPayService = allPayService.stream().filter(item -> !list.contains(item.getPayserviceId())).collect(Collectors.toList());
        }
        // TODO 上一次关闭时间
        return null;
    }
}
