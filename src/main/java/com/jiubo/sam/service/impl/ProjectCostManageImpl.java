package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.PaPayserviceDao;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.dao.PayserviceDao;
import com.jiubo.sam.dao.ProjectCostManageDao;
import com.jiubo.sam.dto.ClosedPro;
import com.jiubo.sam.dto.ClosedProDto;
import com.jiubo.sam.dto.ClosedProListDto;
import com.jiubo.sam.dto.UpdateProDto;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.dto.OpenPro;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.ProjectCostManageService;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.sql.Wrapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectCostManageImpl extends ServiceImpl<ProjectCostManageDao, ProjectCostManageBean> implements ProjectCostManageService {
    @Autowired
    private ProjectCostManageDao projectCostManageDao;

    @Autowired
    private PayserviceDao payserviceDao;

    @Autowired
    private PaPayserviceDao paPayserviceDao;

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
    public List<ClosedPro> getClosedProByPID(Integer id) throws MessageException {
        List<PaPayserviceBean> toRemovePro = projectCostManageDao.getToRemovePro(id);
        List<PayserviceBean> allPayService = projectCostManageDao.getAllPayService();
        List<PayserviceBean> resetList;
        if (!CollectionUtils.isEmpty(toRemovePro)) {
            List<String> list = toRemovePro.stream().map(PaPayserviceBean::getPayserviceId).distinct().collect(Collectors.toList());
            resetList = allPayService.stream().filter(item -> !list.contains(item.getPayserviceId())).collect(Collectors.toList());
        } else {
            resetList = allPayService;
        }
        List<ClosedPro> closedProList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resetList)) {
            for (PayserviceBean payserviceBean : resetList) {
                ClosedPro closedPro = new ClosedPro();
                PaPayserviceBean nextCloseDate = projectCostManageDao.getNextCloseDate(id, Integer.parseInt(payserviceBean.getPayserviceId()));
                closedPro.setPayType(Integer.parseInt(payserviceBean.getPayType()));
                closedPro.setProName(payserviceBean.getName());
                closedPro.setId(Integer.parseInt(payserviceBean.getPayserviceId()));
                closedPro.setUnitPrice(new BigDecimal(payserviceBean.getPrice()));
                if (null != nextCloseDate) {
                    if (nextCloseDate.getEndDate() == null){
                        throw new MessageException("数据错误");
                    }else {
                        closedPro.setNextCloseDate(nextCloseDate.getEndDate());
                    }
                    closedPro.setUnitPrice(new BigDecimal(nextCloseDate.getUnitPrice()));
                }
                closedProList.add(closedPro);
            }
        }
        return closedProList;
    }

    @Override
    public List<OpenPro> getOpenProByPID(Integer id) {
        return projectCostManageDao.getOpenPro(id);
    }

    @Override
    public void closedPro(ClosedProListDto closedProListDto) throws Exception {
        List<ClosedProDto> closedProDtoList = closedProListDto.getClosedProDtoList();
        for (ClosedProDto closedProDto : closedProDtoList) {
            String endDate = closedProDto.getEndDate();
            PaPayserviceBean paPayserviceBean = paPayserviceDao.selectById(closedProDto.getId());
            String begDate = paPayserviceBean.getBegDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
            Date endDateD = simpleDateFormat.parse(endDate);
            Date begDateD = simpleDateFormat.parse(begDate);
            Date now = new Date();
            if (begDateD.before(endDateD) || begDateD.equals(endDateD)) {
                //表示begDateD小于endDateD 或者等于
                if (endDateD.before(now) || endDateD.equals(now)) {
                    //表示endDateD小于现在 或者等于
                    String hospNum = paPayserviceBean.getHospNum();
                    String payserviceId = paPayserviceBean.getPayserviceId();
                    List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectByHospNum(hospNum, endDate, payserviceId);
                    if (!CollectionUtils.isEmpty(paPayserviceBeans)) {
                        throw new MessageException("结束时间未完，所以不能关闭");
                    }
                    paPayserviceBean.setEndDate(endDate);
                    paPayserviceBean.setIsUse("0");
                    paPayserviceDao.updateById(paPayserviceBean);
                } else {
                    throw new MessageException("结束时间必须小于或者等于当前时间");
                }
            } else {
                throw new MessageException("开始时间必须小于或者等于结束时间");
            }


        }
    }

    @Override
    public void updatePro(UpdateProDto updateProDto) throws Exception {
        Integer id = updateProDto.getId();
        PaPayserviceBean paPayserviceBean = paPayserviceDao.selectById(id);
        String begDate = updateProDto.getBegDate();
        String endDate = updateProDto.getEndDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
        if (endDate != null) {
            String unitPrice = updateProDto.getUnitPrice();
            Date endDateD = simpleDateFormat.parse(endDate);
            Date begDateD = simpleDateFormat.parse(begDate);
            Date now = new Date();
            if (begDateD.before(endDateD) || begDateD.equals(endDateD)) {
                //表示begDateD小于endDateD 或者等于
                if (endDateD.before(now) || endDateD.equals(now)) {
                    //表示endDateD小于现在 或者等于
                    String hospNum = paPayserviceBean.getHospNum();
                    String payserviceId = paPayserviceBean.getPayserviceId();
                    List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectByHospNum(hospNum, endDate, payserviceId);
                    if (!CollectionUtils.isEmpty(paPayserviceBeans)) {
                        throw new MessageException("结束时间未完，所以不能修改");
                    }
                    List<PaPayserviceBean> paPayserviceBeanList = paPayserviceDao.selectByHospNum(hospNum, begDate, payserviceId);
                    if (!CollectionUtils.isEmpty(paPayserviceBeanList)) {
                        throw new MessageException("开始时间未完，所以不能修改");
                    }
                    List<PaPayserviceBean> paPayserviceBeanList1 = paPayserviceDao.selectByHospNumAndPayServiceId(hospNum, payserviceId);
                    for (PaPayserviceBean payserviceBean : paPayserviceBeanList1) {
                        Date begparse = simpleDateFormat.parse(payserviceBean.getBegDate());
                        Date endparse = simpleDateFormat.parse(payserviceBean.getEndDate());
                        if ((begDateD.before(begparse) || begDateD.equals(begparse)) && (endparse.before(endDateD) || endparse.before(endDateD))) {
                            throw new MessageException("开始和结束时间未完，所以不能修改");
                        }
                    }
                    paPayserviceBean.setBegDate(begDate);
                    paPayserviceBean.setEndDate(endDate);
                    paPayserviceBean.setUnitPrice(unitPrice);
                    paPayserviceDao.updateById(paPayserviceBean);
                    //退费计算待定



                } else {
                    throw new MessageException("结束时间必须小于或者等于当前时间");
                }
            } else {
                throw new MessageException("开始时间必须小于或者等于结束时间");
            }
        } else {
            String unitPrice = updateProDto.getUnitPrice();
            String hospNum = paPayserviceBean.getHospNum();
            String payserviceId = paPayserviceBean.getPayserviceId();
            Date begDateD = simpleDateFormat.parse(begDate);
            Date now = new Date();
            List<PaPayserviceBean> paPayserviceBeanList = paPayserviceDao.selectByHospNum(hospNum, begDate, payserviceId);
            if (!CollectionUtils.isEmpty(paPayserviceBeanList)) {
                throw new MessageException("开始时间未完，所以不能修改");
            }
            if (((now.before(begDateD)) || (now.equals(begDateD)))){
                throw new MessageException("开始时间要小于等于现在时间");
            }
            paPayserviceBean.setBegDate(begDate);
            paPayserviceBean.setUnitPrice(unitPrice);
            paPayserviceDao.updateById(paPayserviceBean);

            //退费计算待定



        }
    }
}
