package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.LogRecordsBean;
import com.jiubo.sam.bean.MedicalExpensesBean;
import com.jiubo.sam.dao.LogRecordsDao;
import com.jiubo.sam.dao.MedicalExpensesDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.MedicalExpensesService;
import com.jiubo.sam.service.PaymentService;
import com.jiubo.sam.util.CollectionsUtils;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class MedicalExpensesServiceImpl extends ServiceImpl<MedicalExpensesDao, MedicalExpensesBean> implements MedicalExpensesService {


    @Autowired
    private MedicalExpensesDao medicalExpensesDao;

    @Autowired
    private LogRecordsService logRecordsService;

    @Autowired
    private PaymentService paymentService;

    @Override
    public List<MedicalExpensesBean> queryMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws Exception {
        if (StringUtils.isNotBlank(medicalExpensesBean.getEndCreateDate())) {
            medicalExpensesBean.setEndCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.parseAnyDate(medicalExpensesBean.getEndCreateDate())));
        }
        if (StringUtils.isNotBlank(medicalExpensesBean.getSpEndDate())) {
            medicalExpensesBean.setSpEndDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.parseAnyDate(medicalExpensesBean.getSpEndDate())));
        }

        if (StringUtils.isNotBlank(medicalExpensesBean.getHospNum()) && StringUtils.isNotBlank(medicalExpensesBean.getDeptId())){

        }else {
            //判断筛选的患者账号是是否使用部门条件的判断
            if (paymentService.jurdgePatientInDept(medicalExpensesBean.getHospNum(),medicalExpensesBean.getDeptList()) !=true){
                medicalExpensesBean.setDeptId(null);
                medicalExpensesBean.setDeptList(null);
            }
        }


        List<MedicalExpensesBean> medicalExpensesBeans = medicalExpensesDao.queryMedicalExpenses(medicalExpensesBean);
        // 孙云龙修改
//        if (!CollectionsUtils.isEmpty(medicalExpensesBeans)) {
//            for (MedicalExpensesBean bean : medicalExpensesBeans) {
//                if (bean.getIsInHospital() == 1) {
//                    bean.setIsInHospitalLabel("在院");
//                } else {
//                    bean.setIsInHospitalLabel("出院");
//                }
//            }
//        }
        // end
        return medicalExpensesBeans;
    }

    @Override
    public void addMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws Exception {
        String newDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(new Date());
        medicalExpensesBean.setCreateDate(newDate);
        if (medicalExpensesDao.insert(medicalExpensesBean) <= 0) {
            throw new MessageException("操作失败!");
        }

        //添加日志
        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(medicalExpensesBean.getHospNum())
                .setOperateId(Integer.valueOf(medicalExpensesBean.getAccountId()))
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule("医疗费缴费")
                .setOperateType("添加")
                .setLrComment(medicalExpensesBean.toString())
                );
    }

    @Override
    public void updateMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws Exception {
        if (medicalExpensesDao.updateById(medicalExpensesBean) <= 0) {
            throw new MessageException("操作失败!");
        }

        //添加日志
        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(medicalExpensesBean.getHospNum())
                .setOperateId(Integer.valueOf(medicalExpensesBean.getAccountId()))
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule("医疗费缴费")
                .setOperateType("修改")
                .setLrComment(medicalExpensesBean.toString())
        );
        log.debug(new LogRecordsBean()
                .setHospNum(medicalExpensesBean.getHospNum())
                .setOperateId(Integer.valueOf(medicalExpensesBean.getAccountId()))
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule("医疗费缴费")
                .setOperateType("修改")
                .setLrComment(medicalExpensesBean.toString()).toString());
    }
}
