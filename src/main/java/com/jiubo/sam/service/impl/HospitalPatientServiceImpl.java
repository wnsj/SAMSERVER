package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.*;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.PaymentDetailsService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.SerialNumberUtil;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HospitalPatientServiceImpl implements HospitalPatientService {

    @Autowired
    private HospitalPatientDao hospitalPatientDao;

    @Autowired
    private PatinetMarginDao patinetMarginDao;

    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @Autowired
    private PrintsDao printsDao;

    @Autowired
    private PrintDetailsDao printDetailsDao;

    @Autowired
    private PaymentDetailsDao paymentDetailsDao;

    @Autowired
    private LogRecordsService logRecordsService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addHospitalPatient(HospitalPatientBean hospitalPatientBean) throws Exception {

        if (StringUtils.isEmpty(hospitalPatientBean.getHospNum())) {
            throw new MessageException("住院号为空,不可操作");
        }

        if (null == hospitalPatientBean.getConsumType()) {
            throw new MessageException("缴退类型不可为空");
        }

        LocalDateTime now = LocalDateTime.now();

        hospitalPatientBean.setCreateDate(now);

        Date date = hospitalPatientBean.getPayDate();
        Date hisPayDate = hospitalPatientBean.getHisPayDate();
        Instant instant = hisPayDate.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = instant.atZone(zoneId).toLocalDateTime();


        //对于住院和门诊的基础缴费信息设置
        PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
        paymentDetailsBean.setType(hospitalPatientBean.getType())
                .setHospNum(hospitalPatientBean.getHospNum())
                .setPayDate(date)
                .setHisPayDate(hisPayDate)
                .setIdCard(hospitalPatientBean.getIdCard())
                .setCreateDate(hospitalPatientBean.getCreateDate())
                .setDeptId(hospitalPatientBean.getDeptId())
                .setIsInHospital(hospitalPatientBean.getIsInHospital())
                .setRemarks(hospitalPatientBean.getRemarks())
                .setMarginType(1);
        //获取当前账号的余额
        List<PaymentDetailsBean> pdbl = paymentDetailsDao.findPaymentDetailLastByHos(hospitalPatientBean.getHospNum());
        if (pdbl.size() > 0) {
            paymentDetailsBean.setCurrentMargin(pdbl.get(0).getCurrentMargin());
        } else {

            paymentDetailsBean.setCurrentMargin(0D);
        }

        if (!StringUtils.isEmpty(hospitalPatientBean.getEmpId())) {
            paymentDetailsBean.setEmpId(hospitalPatientBean.getEmpId());
        }

        //查询在住院和门诊时此患者是否有交过押金
//        QueryWrapper<PatinetMarginBean> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("HOSP_NUM", hospitalPatientBean.getHospNum());
        List<PatinetMarginBean> list = patinetMarginDao.selecAllList(hospitalPatientBean.getHospNum());
        String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        Integer count = paymentDetailsDao.selectByHospNum(hospitalPatientBean.getType(), formatDate);
        String serialNumber;
        if (CollectionUtils.isEmpty(list)) {
            PatinetMarginBean patinetMarginBean = new PatinetMarginBean();
            patinetMarginBean.setCreateDate(hospitalPatientBean.getCreateDate());
            patinetMarginBean.setModifyDate(dateTime);
            patinetMarginBean.setFlag(2);
            patinetMarginBean.setIdCard(hospitalPatientBean.getIdCard());
            patinetMarginBean.setHospNum(hospitalPatientBean.getHospNum());
            if (hospitalPatientBean.getType().equals(1)) {
                serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "Z", count);
                //是住院花费给住院花费字段添加数据
                paymentDetailsBean.setHospitalUse(hospitalPatientBean.getRealCross());
            } else {
                serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "M", count);
                //是门诊花费给门诊花费字段添加数据
                paymentDetailsBean.setPatientUse(hospitalPatientBean.getRealCross());
            }
            patinetMarginBean.setMoney(-hospitalPatientBean.getRealCross());
            paymentDetailsBean.setCurrentMargin(patinetMarginBean.getMoney());

            //对于没有添加过押金的患者进行押金数据初始化
            if (patinetMarginDao.insert(patinetMarginBean) <= 0) {
                throw new MessageException("添加押金操作失败!");
            }

        } else {
            PatinetMarginBean patinetMarginBean = list.get(0);
            patinetMarginBean.setIdCard(hospitalPatientBean.getIdCard());
            if (hospitalPatientBean.getType().equals(1)) {
                serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "Z", count);
                //住院
                paymentDetailsBean.setHospitalUse(hospitalPatientBean.getRealCross());
            } else {
                // 门诊
                serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "M", count);

                //是门诊花费给门诊花费字段添加数据
                paymentDetailsBean.setPatientUse(hospitalPatientBean.getRealCross());
            }
            patinetMarginBean.setModifyDate(now);
            patinetMarginBean.setMoney(patinetMarginBean.getMoney() - hospitalPatientBean.getRealCross());
            paymentDetailsBean.setCurrentMargin(patinetMarginBean.getMoney());
            patinetMarginDao.updateById(patinetMarginBean);
        }
        hospitalPatientBean.setSerialNumber(serialNumber);
        if (hospitalPatientDao.insert(hospitalPatientBean) <= 0) {
            throw new MessageException("操作失败!");
        }
        paymentDetailsBean.setSerialNumber(serialNumber);
        paymentDetailsBean.setSerialNumberHis(hospitalPatientBean.getSerialNumberHis());
        paymentDetailsBean.setCreator(hospitalPatientBean.getAccountId());
        paymentDetailsBean.setReviser(hospitalPatientBean.getAccountId());
        paymentDetailsBean.setUpdateDate(date);
        paymentDetailsService.addPaymentDetails(paymentDetailsBean);

        //打印
        QueryWrapper<PrintsBean> printBeanQueryWrapper = new QueryWrapper<>();
        printBeanQueryWrapper.eq("TYPE", hospitalPatientBean.getType());
        PrintsBean printBean = printsDao.selectOne(printBeanQueryWrapper);
        PrintDetailsBean printDetailsBean = new PrintDetailsBean();
        printDetailsBean.setDetailId(hospitalPatientBean.getHpId());
        printDetailsBean.setModifyTime(now);
        if (printBean == null) {
            String str = String.format("%03d", 1);
            printBean = new PrintsBean();
            printBean.setType(hospitalPatientBean.getType());
            printBean.setCount(str);
            printBean.setModifyTime(now);
            printsDao.insert(printBean);
            printDetailsBean.setCode(str);
            printDetailsBean.setPrintId(printBean.getId());
        } else {
            printDetailsBean.setPrintId(printBean.getId());
            printBean.setModifyTime(now);
            printBean.setCount(String.format("%03d", Integer.parseInt(printBean.getCount()) + 1));
            printsDao.updateById(printBean);
            printDetailsBean.setCode(printBean.getCount());
        }
        printDetailsDao.insert(printDetailsBean);

        //添加日志
        String module = "";
        if (hospitalPatientBean.getType().equals(1)) {
            module = "住院缴费";
        } else {
            module = "门诊费缴费";
        }

        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(hospitalPatientBean.getHospNum())
                .setOperateId(hospitalPatientBean.getAccountId())
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule(module)
                .setOperateType("添加")
                .setLrComment(hospitalPatientBean.toString())
        );

        return serialNumber;
    }

    @Override
    public String suPay(HospitalPatientBean hospitalPatientBean) throws Exception {
        if (StringUtils.isEmpty(hospitalPatientBean.getHospNum())) {
            throw new MessageException("住院号为空,不可操作");
        }

        if (null == hospitalPatientBean.getConsumType()) {
            throw new MessageException("缴退类型不可为空");
        }

        LocalDateTime now = LocalDateTime.now();

        hospitalPatientBean.setCreateDate(now);

        Date date = hospitalPatientBean.getPayDate();
        String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = instant.atZone(zoneId).toLocalDateTime();

        //对于住院和门诊的基础缴费信息设置
        PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
        paymentDetailsBean.setType(hospitalPatientBean.getType())
                .setHospNum(hospitalPatientBean.getHospNum())
                .setPayDate(date)
                .setIdCard(hospitalPatientBean.getIdCard())
                .setCreateDate(hospitalPatientBean.getCreateDate())
                .setDeptId(hospitalPatientBean.getDeptId())
                .setIsInHospital(hospitalPatientBean.getIsInHospital())
                .setRemarks(hospitalPatientBean.getRemarks())
                .setCreator(hospitalPatientBean.getAccountId())
                .setReviser(hospitalPatientBean.getAccountId())
                .setSerialNumberHis(hospitalPatientBean.getSerialNumberHis())
                .setUpdateDate(new Date())
                .setEmpId(hospitalPatientBean.getEmpId())
                .setMarginType(hospitalPatientBean.getConsumType());

        Integer count = paymentDetailsDao.selectByHospNum(hospitalPatientBean.getType(), formatDate);
        String serialNumber;
        if (hospitalPatientBean.getType().equals(1)) {
            serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "Z", count);
            //是住院花费给住院花费字段添加数据
            paymentDetailsBean.setHospitalUse(hospitalPatientBean.getRealCross());
        } else {
            serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "M", count);
            //是门诊花费给门诊花费字段添加数据
            paymentDetailsBean.setPatientUse(hospitalPatientBean.getRealCross());
        }

        hospitalPatientBean.setSerialNumber(serialNumber);
        if (hospitalPatientDao.insert(hospitalPatientBean) <= 0) {
            throw new MessageException("操作失败!");
        }

        // 流水号
        paymentDetailsBean.setSerialNumber(serialNumber);

        double rc = hospitalPatientBean.getRealCross() == null ? 0 : hospitalPatientBean.getRealCross();
        BigDecimal realCross = new BigDecimal(String.valueOf(rc));
        // 最终余额
        BigDecimal balance;
        PaymentDetailsBean nextBalance = paymentDetailsDao.getNextBalance(hospitalPatientBean.getHospNum(), formatDate);

        if (hospitalPatientBean.getConsumType().equals(1)) {
            // 缴费
            if (null != nextBalance) {
                double ba = nextBalance.getCurrentMargin() == null ? 0 : nextBalance.getCurrentMargin();
                BigDecimal subtract = new BigDecimal(String.valueOf(ba)).subtract(realCross);
                // 最终余额
                balance = subtract;
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(subtract.doubleValue());
            } else {
                BigDecimal multiply = realCross.multiply(new BigDecimal("-1"));
                // 最终余额
                balance = multiply;
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(multiply.doubleValue());
            }
        } else {
            // 退费
            if (null != nextBalance) {
                double ba = nextBalance.getCurrentMargin() == null ? 0 : nextBalance.getCurrentMargin();
                BigDecimal subtract = new BigDecimal(String.valueOf(ba)).add(realCross);
                // 最终余额
                balance = subtract;
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(subtract.doubleValue());
            } else {
                // 最终余额
                balance = realCross;
                // 当前这条明细的余额
                paymentDetailsBean.setCurrentMargin(realCross.doubleValue());
            }
        }

        List<PaymentDetailsBean> lastAll = paymentDetailsDao.getLastAll(hospitalPatientBean.getHospNum(), formatDate);
        paymentDetailsService.addPaymentDetails(paymentDetailsBean);
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
                paymentDetailsService.addPaymentDetails(entity);
                paymentDetailsDao.deleteById(bean.getPdId());
            }
        }

        List<PatinetMarginBean> list = patinetMarginDao.selecAllList(hospitalPatientBean.getHospNum());
        if (!CollectionUtils.isEmpty(list)) {
            PatinetMarginBean marginBean = list.get(0);
            marginBean.setMoney(balance.doubleValue());
            marginBean.setModifyDate(LocalDateTime.now());
            marginBean.setIdCard(hospitalPatientBean.getIdCard());
            marginBean.setPayDate(hospitalPatientBean.getPayDate());
            patinetMarginDao.updateById(marginBean);
        } else {
            PatinetMarginBean patinetMarginBean = new PatinetMarginBean();
            patinetMarginBean.setCreateDate(hospitalPatientBean.getCreateDate());
            patinetMarginBean.setModifyDate(dateTime);
            patinetMarginBean.setFlag(2);
            patinetMarginBean.setIdCard(hospitalPatientBean.getIdCard());
            patinetMarginBean.setHospNum(hospitalPatientBean.getHospNum());
            patinetMarginBean.setMoney(balance.doubleValue());
            if (patinetMarginDao.insert(patinetMarginBean) <= 0) {
                throw new MessageException("添加押金操作失败!");
            }
        }

        //添加日志
        String module = "";
        if (hospitalPatientBean.getType().equals(1)) {
            if (hospitalPatientBean.getConsumType().equals(1)) {
                module = "住院缴费";
            } else {
                module = "住院退费";
            }

        } else {
            if (hospitalPatientBean.getConsumType().equals(1)) {
                module = "门诊费缴费";
            } else {
                module = "门诊费退费";
            }
        }

        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(hospitalPatientBean.getHospNum())
                .setOperateId(hospitalPatientBean.getAccountId())
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule(module)
                .setOperateType("添加")
                .setLrComment(hospitalPatientBean.toString())
        );
        return serialNumber;
    }

    @Override
    public String refundHospitalPatient(HospitalPatientBean hospitalPatientBean) throws Exception {

        if (StringUtils.isEmpty(hospitalPatientBean.getHospNum())) {
            throw new MessageException("住院号为空,不可操作");
        }

        LocalDateTime now = LocalDateTime.now();

        hospitalPatientBean.setCreateDate(now);

        Date date = hospitalPatientBean.getPayDate();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = instant.atZone(zoneId).toLocalDateTime();

        //对于住院和门诊的基础缴费信息设置
        PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
        paymentDetailsBean.setType(hospitalPatientBean.getType())
                .setHospNum(hospitalPatientBean.getHospNum())
                .setPayDate(date)
                .setIdCard(hospitalPatientBean.getIdCard())
                .setCreateDate(hospitalPatientBean.getCreateDate())
                .setDeptId(hospitalPatientBean.getDeptId())
                .setIsInHospital(hospitalPatientBean.getIsInHospital())
                .setRemarks(hospitalPatientBean.getRemarks())
                .setMarginType(2);

        if (!StringUtils.isEmpty(hospitalPatientBean.getEmpId())) {
            paymentDetailsBean.setEmpId(hospitalPatientBean.getEmpId());
        }

        //查询在住院和门诊时此患者是否有交过押金
//        QueryWrapper<PatinetMarginBean> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("HOSP_NUM", hospitalPatientBean.getHospNum());
        List<PatinetMarginBean> list = patinetMarginDao.selecAllList(hospitalPatientBean.getHospNum());
        String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd");
        Integer count = paymentDetailsDao.selectByHospNum(hospitalPatientBean.getType(), formatDate);

        PatinetMarginBean patinetMarginBean = list.get(0);
        patinetMarginBean.setModifyDate(now);
        String serialNumber;
        if (hospitalPatientBean.getType().equals(1)) {

            //是住院花费给住院花费字段添加数据
            serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "Z", count);
            paymentDetailsBean.setHospitalUse(hospitalPatientBean.getRealCross());
        } else {
//            hospitalPatientBean.setAmount(hospitalPatientBean.getRealCross());
            //是门诊花费给门诊花费字段添加数据
            serialNumber = SerialNumberUtil.generateSerialNumber(dateTime, "M", count);
            paymentDetailsBean.setPatientUse(hospitalPatientBean.getRealCross());
        }
        patinetMarginBean.setMoney(patinetMarginBean.getMoney() + hospitalPatientBean.getRealCross());

        paymentDetailsBean.setSerialNumber(serialNumber);
        paymentDetailsBean.setSerialNumberHis(hospitalPatientBean.getSerialNumberHis());
        paymentDetailsBean.setCurrentMargin(patinetMarginBean.getMoney());
        paymentDetailsBean.setReviser(hospitalPatientBean.getAccountId());
        patinetMarginDao.updateById(patinetMarginBean);
        hospitalPatientBean.setSerialNumber(serialNumber);
        if (hospitalPatientDao.insert(hospitalPatientBean) <= 0) {
            throw new MessageException("操作失败!");
        }
        paymentDetailsBean.setCreator(hospitalPatientBean.getAccountId());
        paymentDetailsBean.setReviser(hospitalPatientBean.getAccountId());
        paymentDetailsBean.setUpdateDate(date);
        paymentDetailsService.addPaymentDetails(paymentDetailsBean);

        //打印
        QueryWrapper<PrintsBean> printBeanQueryWrapper = new QueryWrapper<>();
        printBeanQueryWrapper.eq("TYPE", hospitalPatientBean.getType());
        PrintsBean printBean = printsDao.selectOne(printBeanQueryWrapper);
        PrintDetailsBean printDetailsBean = new PrintDetailsBean();
        printDetailsBean.setDetailId(hospitalPatientBean.getHpId());
        printDetailsBean.setModifyTime(now);
        if (printBean == null) {
            String str = String.format("%03d", 1);
            printBean = new PrintsBean();
            printBean.setType(hospitalPatientBean.getType());
            printBean.setCount(str);
            printBean.setModifyTime(now);
            printsDao.insert(printBean);
            printDetailsBean.setCode(str);
            printDetailsBean.setPrintId(printBean.getId());
        } else {
            printDetailsBean.setPrintId(printBean.getId());
            printBean.setModifyTime(now);
            printBean.setCount(String.format("%03d", Integer.parseInt(printBean.getCount()) + 1));
            printsDao.updateById(printBean);
            printDetailsBean.setCode(printBean.getCount());
        }
        printDetailsDao.insert(printDetailsBean);

        //添加日志
        String module = "";
        if (hospitalPatientBean.getType().equals(1)) {
            module = "住院退费";
        } else {
            module = "门诊费退费";
        }

        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum(hospitalPatientBean.getHospNum())
                .setOperateId(hospitalPatientBean.getAccountId())
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule(module)
                .setOperateType("添加")
                .setLrComment(hospitalPatientBean.toString())
        );

        return serialNumber;
    }

    @Override
    public Double findHospitalPatientByHdId(Integer hpId) {
        return hospitalPatientDao.findHospitalPatientByHdId(hpId);
    }

    @Override
    public PageInfo<HospitalPatientBean> findHospitalPatient(HospitalPatientCondition hospitalPatientBean) throws Exception {
        int pageNum = hospitalPatientBean.getPageNum() == null ? 1 : hospitalPatientBean.getPageNum();
        int pageSize = hospitalPatientBean.getPageSize() == null ? 10 : hospitalPatientBean.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<HospitalPatientBean> list = hospitalPatientDao.selectByCondition(hospitalPatientBean);
//        DecimalFormat df = new DecimalFormat("######0.00");
        for (HospitalPatientBean patientBean : list) {
//            Double paCount = patientBean.getPaCount();
//            if (paCount==null){
//                paCount=0D;
//            }
//            Double marginAmount = patientBean.getMarginAmount();
//            if (marginAmount==null){
//                marginAmount=0D;
//            }
//            patientBean.setMarginAmount(Double.valueOf(df.format(marginAmount-paCount)));

            Integer consumType = patientBean.getConsumType();
            if (consumType == 2) {
                patientBean.setRealCross(patientBean.getRealCross() * -1);
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    public void updateHospitalPatient(HospitalPatientBean hospitalPatientBean) {
        HospitalPatientBean patientBean = hospitalPatientDao.selectById(hospitalPatientBean.getHpId());
        QueryWrapper<PatinetMarginBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("HOSP_NUM", hospitalPatientBean.getHospNum());
        List<PatinetMarginBean> list = patinetMarginDao.selectList(queryWrapper);
        PatinetMarginBean patinetMarginBean = list.get(0);
        if (hospitalPatientBean.getType().equals(1)) {
            patinetMarginBean.setMoney(patinetMarginBean.getMoney() + patientBean.getRealCross());
            patinetMarginBean.setModifyDate(LocalDateTime.now());
            patinetMarginBean.setMoney(patinetMarginBean.getMoney() - hospitalPatientBean.getRealCross());
        } else {
            patinetMarginBean.setMoney(patinetMarginBean.getMoney() + patientBean.getAmount());
            patinetMarginBean.setModifyDate(LocalDateTime.now());
            hospitalPatientBean.setAmount(hospitalPatientBean.getRealCross() - hospitalPatientBean.getAmountDeclared());
            patinetMarginBean.setMoney(patinetMarginBean.getMoney() - hospitalPatientBean.getAmount());
        }
        patinetMarginDao.updateById(patinetMarginBean);
        hospitalPatientDao.updateById(hospitalPatientBean);
    }

}
