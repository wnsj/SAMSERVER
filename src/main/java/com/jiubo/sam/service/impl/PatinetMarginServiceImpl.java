package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.*;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.PatinetMarginService;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatinetMarginServiceImpl implements PatinetMarginService {

    @Autowired
    private LogRecordsService logRecordsService;

    @Autowired
    private PatinetMarginDao patinetMarginDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private PaymentDetailsDao paymentDetailsDao;

    @Autowired
    private PrintDetailsDao printDetailsDao;

    @Autowired
    private PrintsDao printsDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPatinetMargin(PatinetMarginBean patinetMarginBean) throws Exception{
        //查询此患者信息
        QueryWrapper<PatientBean> patientBeanQueryWrapper = new QueryWrapper<>();
        patientBeanQueryWrapper.eq("HOSP_NUM",patinetMarginBean.getHospNum());
        List<PatientBean> patientBeanList = patientDao.selectList(patientBeanQueryWrapper);
        if(CollectionUtils.isEmpty(patientBeanList)){
            throw new MessageException("患者数据异常");
        }
        PatientBean patientBean = patientBeanList.get(0);

        //对于住院和门诊的基础缴费信息设置
        PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
        paymentDetailsBean.setType(3)
                .setHospNum(patientBean.getHospNum())
                .setCreateDate(LocalDateTime.now())
                .setDeptId(Integer.valueOf(patientBean.getDeptId()))
                .setIsInHospital(Integer.valueOf(patientBean.getInHosp()))
                .setRemarks(patinetMarginBean.getRemark())
                .setMarginUse(patinetMarginBean.getMoney());
        if(!StringUtils.isEmpty(patientBean.getEmpId())){
            paymentDetailsBean.setEmpId(Integer.valueOf(patientBean.getEmpId()));
        }

        //查询此患者是否交过押金
        patinetMarginBean.setCreateDate(LocalDateTime.now());
        patinetMarginBean.setModifyDate(LocalDateTime.now());
        QueryWrapper<PatinetMarginBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("HOSP_NUM",patinetMarginBean.getHospNum());
        List<PatinetMarginBean> list = patinetMarginDao.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            //设置缴费记录里是添加还是退费
            paymentDetailsBean.setMarginType(1);
            paymentDetailsBean.setCurrentMargin(patinetMarginBean.getMoney());
            if (patinetMarginDao.insert(patinetMarginBean) <= 0) {
                throw new MessageException("操作失败!");
            }
        }else {
            PatinetMarginBean entity = list.get(0);
            if(patinetMarginBean.getType().equals(1)){
                //设置缴费记录里是添加还是退费
                paymentDetailsBean.setMarginType(1);
                entity.setModifyDate(LocalDateTime.now());
                entity.setMoney(entity.getMoney()+patinetMarginBean.getMoney());
                paymentDetailsBean.setCurrentMargin(entity.getMoney());
            }else {
                //设置缴费记录里是添加还是退费
                paymentDetailsBean.setMarginType(2);
                entity.setModifyDate(LocalDateTime.now());
                entity.setMoney(entity.getMoney()-patinetMarginBean.getMoney());
                paymentDetailsBean.setCurrentMargin(entity.getMoney());
            }
            patinetMarginDao.updateById(entity);
        }
        paymentDetailsDao.insert(paymentDetailsBean);

        //打印
        QueryWrapper<PrintsBean> printBeanQueryWrapper = new QueryWrapper<>();
        printBeanQueryWrapper.eq("TYPE",3);
        PrintsBean printBean = printsDao.selectOne(printBeanQueryWrapper);
        PrintDetailsBean printDetailsBean = new PrintDetailsBean();
        printDetailsBean.setDetailId(paymentDetailsBean.getPdId());
        printDetailsBean.setModifyTime(LocalDateTime.now());
        if(printBean == null){
            String str = String.format("%03d",1);
            printBean = new PrintsBean();
            printBean.setType(3);
            printBean.setCount(str);
            printBean.setModifyTime(LocalDateTime.now());
            printsDao.insert(printBean);
            printDetailsBean.setCode(str);
            printDetailsBean.setPrintId(printBean.getId());
        }else {
            printDetailsBean.setPrintId(printBean.getId());
            printBean.setModifyTime(LocalDateTime.now());
            printBean.setCount(String.format("%03d",Integer.parseInt(printBean.getCount())+1));
            printsDao.updateById(printBean);
            printDetailsBean.setCode(printBean.getCount());
        }
        printDetailsDao.insert(printDetailsBean);


        //添加日志
        String module = "";
        if(patinetMarginBean.getType().equals(1)){
            module = "押金缴费";
        }else{
            module = "押金退费";
        }

        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(patinetMarginBean.getHospNum())
                .setOperateId(Integer.valueOf(patinetMarginBean.getAccountId()))
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule(module)
                .setOperateType("添加")
                .setLrComment(patinetMarginBean.toString())
        );
    }
}
