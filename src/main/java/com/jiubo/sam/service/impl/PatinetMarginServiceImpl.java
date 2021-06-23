package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.*;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.PatinetMarginService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.SerialNumberUtil;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
        LocalDateTime dateTime = LocalDateTime.now();
        paymentDetailsBean.setType(3)
                .setHospNum(patientBean.getHospNum())
                .setCreateDate(dateTime)
                .setPayDate(new Date())
                .setDeptId(Integer.valueOf(patientBean.getDeptId()))
                .setIsInHospital(Integer.valueOf(patientBean.getInHosp()))
                .setRemarks(patinetMarginBean.getRemark())
                .setMarginUse(patinetMarginBean.getMoney());
        if(!StringUtils.isEmpty(patientBean.getEmpId())){
            paymentDetailsBean.setEmpId(Integer.valueOf(patientBean.getEmpId()));
        }
        String formatDate = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
        // 流水号
        Integer integer = paymentDetailsDao.selectByHospNum(3,formatDate);
        String serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "Y", integer);
        paymentDetailsBean.setSerialNumber(serialNumber);

        //查询此患者是否交过押金
        patinetMarginBean.setCreateDate(dateTime);
        patinetMarginBean.setModifyDate(dateTime);
       /* QueryWrapper<PatinetMarginBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("HOSP_NUM",patinetMarginBean.getHospNum());
        List<PatinetMarginBean> list = patinetMarginDao.selectList(queryWrapper);*/
        List<PatinetMarginBean> list = patinetMarginDao.selecAllList(patinetMarginBean.getHospNum());

        if(CollectionUtils.isEmpty(list)){
            //设置缴费记录里是添加还是退费
            paymentDetailsBean.setMarginType(1);
            paymentDetailsBean.setCurrentMargin(patinetMarginBean.getMoney());
            paymentDetailsBean.setCreator(patinetMarginBean.getAccountId());
            paymentDetailsBean.setReviser(patinetMarginBean.getAccountId());
            if (patinetMarginDao.insert(patinetMarginBean) <= 0) {
                throw new MessageException("操作失败!");
            }
        }else {
            PatinetMarginBean entity = list.get(0);
            if(patinetMarginBean.getType().equals(1)){
                // 交押金
                paymentDetailsBean.setMarginType(1);
                entity.setModifyDate(dateTime);
                entity.setMoney(entity.getMoney()+patinetMarginBean.getMoney());
            }else {
                // 退押金
                paymentDetailsBean.setMarginType(2);
                entity.setModifyDate(dateTime);
                entity.setMoney(entity.getMoney()-patinetMarginBean.getMoney());
            }
            paymentDetailsBean.setCurrentMargin(entity.getMoney());
            paymentDetailsBean.setReviser(patinetMarginBean.getAccountId());
            patinetMarginDao.updateById(entity);
        }
        paymentDetailsBean.setPayment(patinetMarginBean.getPayment());
        paymentDetailsBean.setCreator(patinetMarginBean.getAccountId());
        paymentDetailsBean.setReviser(patinetMarginBean.getAccountId());
        paymentDetailsDao.insert(paymentDetailsBean);
        //paymentDetailsDao.insertBean(paymentDetailsBean);

        //打印
        QueryWrapper<PrintsBean> printBeanQueryWrapper = new QueryWrapper<>();
        printBeanQueryWrapper.eq("TYPE",3);
        PrintsBean printBean = printsDao.selectOne(printBeanQueryWrapper);
        PrintDetailsBean printDetailsBean = new PrintDetailsBean();
        printDetailsBean.setDetailId(paymentDetailsBean.getPdId());
        printDetailsBean.setModifyTime(dateTime);
        if(printBean == null){
            String str = String.format("%03d",1);
            printBean = new PrintsBean();
            printBean.setType(3);
            printBean.setCount(str);
            printBean.setModifyTime(dateTime);
            printsDao.insert(printBean);
            printDetailsBean.setCode(str);
            printDetailsBean.setPrintId(printBean.getId());
        }else {
            printDetailsBean.setPrintId(printBean.getId());
            printBean.setModifyTime(dateTime);
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
                .setOperateId(patinetMarginBean.getAccountId())
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule(module)
                .setOperateType("添加")
                .setLrComment(patinetMarginBean.toString())
        );
    }
}
