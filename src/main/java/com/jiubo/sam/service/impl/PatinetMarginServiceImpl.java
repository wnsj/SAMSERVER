package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.*;
import com.jiubo.sam.dto.RemarkDto;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.PatinetMarginService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.SerialNumberUtil;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public void addPatinetMargin(PatinetMarginBean patinetMarginBean) throws Exception {

        if (StringUtils.isEmpty(patinetMarginBean.getHospNum())) {
            throw new MessageException("住院号不可为空");
        }

        if (patinetMarginBean.getType() == null) {
            throw new MessageException("缴退类型不可为空");
        }

        //查询此患者信息
        QueryWrapper<PatientBean> patientBeanQueryWrapper = new QueryWrapper<>();
        patientBeanQueryWrapper.eq("HOSP_NUM", patinetMarginBean.getHospNum());
        List<PatientBean> patientBeanList = patientDao.selectList(patientBeanQueryWrapper);
        if (CollectionUtils.isEmpty(patientBeanList)) {
            throw new MessageException("患者数据异常");
        }
        PatientBean patientBean = patientBeanList.get(0);

        //对于住院和门诊的基础缴费信息设置
        PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
        LocalDateTime dateTime = LocalDateTime.now();
        paymentDetailsBean.setType(3)
                .setHospNum(patientBean.getHospNum())
                .setCreateDate(dateTime)
                .setIdCard(patientBean.getIdCard())
                .setPayDate(patinetMarginBean.getPayDate())
                .setDeptId(Integer.valueOf(patientBean.getDeptId()))
                .setIsInHospital(Integer.valueOf(patientBean.getInHosp()))
                .setRemarks(patinetMarginBean.getRemark())
                .setMarginUse(patinetMarginBean.getMoney());
        if (!StringUtils.isEmpty(patientBean.getEmpId())) {
            paymentDetailsBean.setEmpId(Integer.valueOf(patientBean.getEmpId()));
        }
        String formatDate = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
        // 流水号
        Integer integer = paymentDetailsDao.selectByHospNum(3, formatDate);
        String serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "Y", integer);
        paymentDetailsBean.setSerialNumber(serialNumber);

        //查询此患者是否交过押金
        patinetMarginBean.setIdCard(patientBean.getIdCard());
       /* QueryWrapper<PatinetMarginBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("HOSP_NUM",patinetMarginBean.getHospNum());
        List<PatinetMarginBean> list = patinetMarginDao.selectList(queryWrapper);*/
        List<PatinetMarginBean> list = patinetMarginDao.selecAllList(patinetMarginBean.getHospNum());

        if (CollectionUtils.isEmpty(list)) {
            patinetMarginBean.setCreateDate(dateTime);
            patinetMarginBean.setModifyDate(dateTime);
            patinetMarginBean.setFlag(2);
            //设置缴费记录里是添加还是退费
            if (patinetMarginBean.getType().equals(1)) {
                paymentDetailsBean.setMarginType(1);
            } else {
                paymentDetailsBean.setMarginType(2);
            }
            paymentDetailsBean.setMarginType(1);
            paymentDetailsBean.setCurrentMargin(patinetMarginBean.getMoney());
            paymentDetailsBean.setCreator(patinetMarginBean.getAccountId());
            paymentDetailsBean.setReviser(patinetMarginBean.getAccountId());
            if (patinetMarginDao.insert(patinetMarginBean) <= 0) {
                throw new MessageException("操作失败!");
            }
        } else {
            PatinetMarginBean entity = list.get(0);
            if (patinetMarginBean.getType().equals(1)) {
                // 交押金
                paymentDetailsBean.setMarginType(1);
                entity.setModifyDate(dateTime);
                entity.setIdCard(patientBean.getIdCard());
                BigDecimal money = new BigDecimal("0");
                if (null != entity.getMoney() && entity.getMoney() != 0) {
                    money = new BigDecimal(String.valueOf(entity.getMoney()));
                }
                BigDecimal add = money.add(new BigDecimal(String.valueOf(patinetMarginBean.getMoney())));
                entity.setMoney(add.doubleValue());
            } else {
                // 退押金
                paymentDetailsBean.setMarginType(2);
                entity.setModifyDate(dateTime);
                BigDecimal money = new BigDecimal("0");
                if (null != entity.getMoney() && entity.getMoney() != 0) {
                    money = new BigDecimal(String.valueOf(entity.getMoney()));
                }
                BigDecimal subtract = money.subtract(new BigDecimal(String.valueOf(patinetMarginBean.getMoney())));
                entity.setMoney(subtract.doubleValue());
            }
            paymentDetailsBean.setCurrentMargin(entity.getMoney());
            paymentDetailsBean.setReviser(patinetMarginBean.getAccountId());
            patinetMarginDao.updateById(entity);
        }
        paymentDetailsBean.setPayment(patinetMarginBean.getPayment());
        paymentDetailsBean.setCreator(patinetMarginBean.getAccountId());
        paymentDetailsBean.setReviser(patinetMarginBean.getAccountId());
        paymentDetailsBean.setIdCard(patinetMarginBean.getIdCard());
        paymentDetailsDao.insert(paymentDetailsBean);

        //打印
        QueryWrapper<PrintsBean> printBeanQueryWrapper = new QueryWrapper<>();
        printBeanQueryWrapper.eq("TYPE", 3);
        PrintsBean printBean = printsDao.selectOne(printBeanQueryWrapper);
        PrintDetailsBean printDetailsBean = new PrintDetailsBean();
        printDetailsBean.setDetailId(paymentDetailsBean.getPdId());
        printDetailsBean.setModifyTime(dateTime);
        if (printBean == null) {
            String str = String.format("%03d", 1);
            printBean = new PrintsBean();
            printBean.setType(3);
            printBean.setCount(str);
            printBean.setModifyTime(dateTime);
            printsDao.insert(printBean);
            printDetailsBean.setCode(str);
            printDetailsBean.setPrintId(printBean.getId());
        } else {
            printDetailsBean.setPrintId(printBean.getId());
            printBean.setModifyTime(dateTime);
            printBean.setCount(String.format("%03d", Integer.parseInt(printBean.getCount()) + 1));
            printsDao.updateById(printBean);
            printDetailsBean.setCode(printBean.getCount());
        }
        printDetailsDao.insert(printDetailsBean);


        //添加日志
        String module = "";
        if (patinetMarginBean.getType().equals(1)) {
            module = "预交金缴费";
        } else {
            module = "预交金退费";
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

    @Override
    public String suMargin(PatinetMarginBean patinetMarginBean) throws Exception {

        if (StringUtils.isEmpty(patinetMarginBean.getHospNum())) {
            throw new MessageException("住院号为空,不可操作");
        }

        if (null == patinetMarginBean.getType()) {
            throw new MessageException("缴退类型不可为空");
        }

        //查询此患者信息
        QueryWrapper<PatientBean> patientBeanQueryWrapper = new QueryWrapper<>();
        patientBeanQueryWrapper.eq("HOSP_NUM", patinetMarginBean.getHospNum());
        List<PatientBean> patientBeanList = patientDao.selectList(patientBeanQueryWrapper);
        if (CollectionUtils.isEmpty(patientBeanList)) {
            throw new MessageException("患者数据异常");
        }
        PatientBean patientBean = patientBeanList.get(0);

        LocalDateTime now = LocalDateTime.now();

        patinetMarginBean.setCreateDate(now);

        Date date = patinetMarginBean.getPayDate();
        String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = instant.atZone(zoneId).toLocalDateTime();

        //对于住院和门诊的基础缴费信息设置
        PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
        paymentDetailsBean.setType(3)
                .setHospNum(patientBean.getHospNum())
                .setPayDate(date)
                .setIdCard(patinetMarginBean.getIdCard())
                .setCreateDate(patinetMarginBean.getCreateDate())
                .setDeptId(Integer.parseInt(patientBean.getDeptId()))
                .setIsInHospital(Integer.parseInt(patientBean.getInHosp()))
                .setRemarks(patinetMarginBean.getRemark())
                .setCreator(patinetMarginBean.getAccountId())
                .setReviser(patinetMarginBean.getAccountId())
                .setUpdateDate(new Date())
                .setPayment(patinetMarginBean.getPayment())
                .setMarginUse(patinetMarginBean.getMoney())
                .setMarginType(patinetMarginBean.getType());

        if (!StringUtils.isEmpty(patientBean.getEmpId())) {
            paymentDetailsBean.setEmpId(Integer.parseInt(patientBean.getEmpId()));
        }
        Integer count = paymentDetailsDao.selectByHospNum(3, formatDate);
        String serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "Y", count);

        // 流水号
        paymentDetailsBean.setSerialNumber(serialNumber);

        double rc = patinetMarginBean.getMoney() == null ? 0 : patinetMarginBean.getMoney();
        BigDecimal realCross = new BigDecimal(String.valueOf(rc));
        // 最终余额
        BigDecimal balance;
        PaymentDetailsBean nextBalance = paymentDetailsDao.getNextBalance(patientBean.getHospNum(), formatDate);

        if (patinetMarginBean.getType().equals(1)) {
            // 交押金
            if (null != nextBalance) {
                double ba = nextBalance.getCurrentMargin() == null ? 0 : nextBalance.getCurrentMargin();
                BigDecimal add = new BigDecimal(String.valueOf(ba)).add(realCross);
                // 最终余额
                balance = add;
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(add.doubleValue());
            } else {
                // 最终余额
                balance = realCross;
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(realCross.doubleValue());
            }
        } else {
            // 退押金
            if (null != nextBalance) {
                double ba = nextBalance.getCurrentMargin() == null ? 0 : nextBalance.getCurrentMargin();
                BigDecimal subtract = new BigDecimal(String.valueOf(ba)).subtract(realCross);
                // 最终余额
                balance = subtract;
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(subtract.doubleValue());
            } else {
                // 最终余额
                balance = realCross.multiply(new BigDecimal("-1"));
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(realCross.doubleValue());
            }
        }

        List<PaymentDetailsBean> lastAll = paymentDetailsDao.getLastAll(patientBean.getHospNum(), formatDate);
        paymentDetailsDao.insert(paymentDetailsBean);
        if (!CollectionUtils.isEmpty(lastAll)) {
            for (PaymentDetailsBean bean : lastAll) {
                PaymentDetailsBean entity = new PaymentDetailsBean();
                BeanUtils.copyProperties(bean, entity);
                entity.setPdId(null);
                double charge;
                if (bean.getType().equals(1)) {
                    charge = bean.getHospitalUse() == null ? 0 : bean.getHospitalUse();
                } else if (bean.getType().equals(2)) {
                    charge = bean.getPatientUse() == null ? 0 : bean.getPatientUse();
                } else {
                    charge = bean.getMarginUse() == null ? 0 : bean.getMarginUse();
                }
                if (bean.getMarginType().equals(1)) {
                    if (bean.getType().equals(3)) {
                        balance = balance.add(new BigDecimal(String.valueOf(charge)));
                    } else {
                        balance = balance.subtract(new BigDecimal(String.valueOf(charge)));
                    }
                } else {
                    if (bean.getType().equals(3)) {
                        balance = balance.subtract(new BigDecimal(String.valueOf(charge)));
                    } else {
                        balance = balance.add(new BigDecimal(String.valueOf(charge)));
                    }
                }
                entity.setCurrentMargin(balance.doubleValue());
                paymentDetailsDao.insert(entity);
                paymentDetailsDao.deleteById(bean.getPdId());
            }
        }

        List<PatinetMarginBean> list = patinetMarginDao.selecAllList(patientBean.getHospNum());
        if (!CollectionUtils.isEmpty(list)) {
            PatinetMarginBean marginBean = list.get(0);
            marginBean.setMoney(balance.doubleValue());
            marginBean.setModifyDate(LocalDateTime.now());
            marginBean.setIdCard(patientBean.getIdCard());
            marginBean.setPayDate(patinetMarginBean.getPayDate());
            patinetMarginDao.updateById(marginBean);
        } else {
            patinetMarginBean.setModifyDate(dateTime);
            patinetMarginBean.setFlag(2);
            patinetMarginBean.setMoney(balance.doubleValue());
            if (patinetMarginDao.insert(patinetMarginBean) <= 0) {
                throw new MessageException("添加押金操作失败!");
            }
        }

        //添加日志
        String module = "";
        if (patinetMarginBean.getType().equals(1)) {
            module = "预交金缴费";
        } else {
            module = "预交金退费";
        }

        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(patinetMarginBean.getHospNum())
                .setOperateId(patinetMarginBean.getAccountId())
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule(module)
                .setOperateType("添加")
                .setLrComment(patinetMarginBean.toString())
        );
        return serialNumber;
    }

    @Override
    public int updateMarginRemark(RemarkDto remarkDto) throws MessageException {
        if (null == remarkDto.getId()) {
            throw new MessageException("id不能为空");
        }
        return patinetMarginDao.patchMarginRemarkById(remarkDto.getId(), remarkDto.getRemark());
    }

    @Override
    public int updateMeRemark(RemarkDto remarkDto) throws MessageException {
        if (null == remarkDto.getId()) {
            throw new MessageException("id不能为空");
        }
        return patinetMarginDao.patchMeRemarkById(remarkDto.getId(), remarkDto.getRemark());
    }
}
