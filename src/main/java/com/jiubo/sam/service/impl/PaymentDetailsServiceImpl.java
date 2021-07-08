package com.jiubo.sam.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.PaPayserviceDao;
import com.jiubo.sam.dao.PaymentDetailsDao;
import com.jiubo.sam.dto.MedicalAmount;
import com.jiubo.sam.dto.PaymentDetailsDto;
import com.jiubo.sam.dto.PdByPIdDto;
import com.jiubo.sam.dto.PdCondition;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.service.PaymentDetailsService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.neethi.PolicyRegistryImpl;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

    @Autowired
    private PaymentDetailsDao paymentDetailsDao;

    @Autowired
    private HospitalPatientService hospitalPatientService;

    @Autowired
    private PaPayserviceDao paPayserviceDao;

    @Autowired
    private DataSource ds;

    @Override
    public void addPaymentDetails(PaymentDetailsBean paymentDetailsBean) throws MessageException {
        if (paymentDetailsDao.insert(paymentDetailsBean) <= 0) {
            throw new MessageException("添加缴费记录操作失败!");
        }
    }

    @Override
    public Object findPaymentDetail(HospitalPatientCondition hospitalPatientCondition) {
        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto();
        BigDecimal hospitalUseTotal = BigDecimal.ZERO;//住院发生合计
        BigDecimal marginUseTotal = BigDecimal.ZERO;//预交金缴费合计
        BigDecimal patientUseUseTotal = BigDecimal.ZERO;//门诊发生合计
        List<PaymentDetailsBean> lists = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        for (PaymentDetailsBean paymentDetailsBean : lists) {
            // 住院费缴费统计
            hospitalUseTotal =  hospitalUseTotal.add(new BigDecimal(String.valueOf(paymentDetailsBean.getHospitalUse())));

            //预交金缴费合计
            marginUseTotal = marginUseTotal.add(new BigDecimal(String.valueOf(paymentDetailsBean.getMarginUse())));

            //门诊发生合计
            patientUseUseTotal = patientUseUseTotal.add(new BigDecimal(String.valueOf(paymentDetailsBean.getPatientUse())));
        }
        paymentDetailsDto.setHospitalUseTotal(hospitalUseTotal.doubleValue());
        paymentDetailsDto.setMarginUseTotal(marginUseTotal.doubleValue());
        paymentDetailsDto.setPatientUseUseTotal(patientUseUseTotal.doubleValue());

        int pageNum = hospitalPatientCondition.getPageNum() == null ? 1 : hospitalPatientCondition.getPageNum();
        int pageSize = hospitalPatientCondition.getPageSize() == null ? 10 : hospitalPatientCondition.getPageSize();


        PageHelper.startPage(pageNum, pageSize);
        List<PaymentDetailsBean> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
        PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
        paymentDetailsDto.setList(result);

        return paymentDetailsDto;
    }

    @Override
    public Object findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition) {
        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto();
        Double hospitalUseTotal = 0D;//住院发生合计
        Double marginUseTotal = 0D;//预交金缴费合计
        Double patientUseUseTotal = 0D;//门诊发生合计
        Double marginAmountTotal = 0D;//余额合计
        BigDecimal noMeUseTotal = new BigDecimal("0");
        List<PaymentDetailsBean> lists = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        for (PaymentDetailsBean paymentDetailsBean : lists) {
            Double hospitalUse = paymentDetailsBean.getHospitalUse();
            if (hospitalUse == null) {
                hospitalUse = 0D;
            }//住院发生合计
            hospitalUseTotal += hospitalUse;

            Double marginUse = paymentDetailsBean.getMarginUse();
            if (marginUse == null) {
                marginUse = 0D;
            }//预交金缴费合计
            marginUseTotal += marginUse;

            Double patientUse = paymentDetailsBean.getPatientUse();
            if (patientUse == null) {
                patientUse = 0D;
            }//门诊发生合计
            patientUseUseTotal += patientUse;


            BigDecimal noMeUse = paymentDetailsBean.getNoMeUse();
            if (noMeUse == null) {
                noMeUse = new BigDecimal("0");
            }//非医疗发生合计
            noMeUseTotal = noMeUseTotal.add(noMeUse);


            double marginAmount = 0D;
            String amount = paymentDetailsBean.getMarginAmount();
            if (!StringUtils.isEmpty(amount)) {
                marginAmount = Double.parseDouble(amount);
            }
            //余额合计
            marginAmountTotal += marginAmount;

        }
        marginAmountTotal = (double) Math.round(marginAmountTotal * 100) / 100;
        hospitalUseTotal = (double) Math.round(hospitalUseTotal * 100) / 100;
        marginUseTotal = (double) Math.round(marginUseTotal * 100) / 100;
        patientUseUseTotal = (double) Math.round(patientUseUseTotal * 100) / 100;

        paymentDetailsDto.setHospitalUseTotal(hospitalUseTotal);
        paymentDetailsDto.setMarginUseTotal(marginUseTotal);
        paymentDetailsDto.setPatientUseUseTotal(patientUseUseTotal);
        paymentDetailsDto.setMarginAmountUseTotal(marginAmountTotal);
        paymentDetailsDto.setNonMedicalUseTotal(noMeUseTotal);
        Integer pageNum = hospitalPatientCondition.getPageNum() == null ? 1 : hospitalPatientCondition.getPageNum();
        Integer pageSize = hospitalPatientCondition.getPageSize() == null ? 10 : hospitalPatientCondition.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        Integer isInHospital = hospitalPatientCondition.getIsInHospital();

        /*if (isInHospital==2){
            hospitalPatientCondition.setIsInHospital(0);
        }*/

        List<PaymentDetailsBean> list = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
        paymentDetailsDto.setList(result);
        return paymentDetailsDto;
    }

    @Override
    public MedicalAmount getMedicalAmount(HospitalPatientCondition condition) throws Exception {
        Integer isNew = condition.getIsNew();
        if (isNew != null) {
            condition.setIsNew(null);
        }
        MedicalAmount medicalAmount = new MedicalAmount();
        HospitalPatientCondition outpatientTotal = new HospitalPatientCondition();
        BeanUtils.copyProperties(condition, outpatientTotal);
        outpatientTotal.setType(2);
        List<HospitalPatientBean> outpatientTotalBean = paymentDetailsDao.findMedicalAmount(outpatientTotal);
        Double outpatientTotalNum = 0D;
        for (HospitalPatientBean hospitalPatientBean : outpatientTotalBean) {

            Double realCross = hospitalPatientBean.getRealCross();
            if (hospitalPatientBean.getConsumType() == 2) {
                realCross = realCross * -1;
            }
            outpatientTotalNum += realCross;
        }
        outpatientTotalNum = (double) Math.round(outpatientTotalNum * 100) / 100;
        BigDecimal decimaloutpatientTotal = new BigDecimal(outpatientTotalNum);
        decimaloutpatientTotal = decimaloutpatientTotal.setScale(2, RoundingMode.HALF_UP);
        medicalAmount.setOutpatientTotal(decimaloutpatientTotal);


        outpatientTotal.setType(1);
        List<HospitalPatientBean> inHospitalTotalBean = paymentDetailsDao.findMedicalAmount(outpatientTotal);
        Double inHospitalTotalNum = 0D;
        for (HospitalPatientBean hospitalPatientBean : inHospitalTotalBean) {
            Double realCross = hospitalPatientBean.getRealCross();
            if (hospitalPatientBean.getConsumType() == 2) {
                realCross = realCross * -1;
            }
            inHospitalTotalNum += realCross;
        }
        inHospitalTotalNum = (double) Math.round(inHospitalTotalNum * 100) / 100;
        BigDecimal decimalinHospitalTotalNum = new BigDecimal(inHospitalTotalNum);
        decimalinHospitalTotalNum = decimalinHospitalTotalNum.setScale(2, RoundingMode.HALF_UP);
        medicalAmount.setInHospitalTotal(decimalinHospitalTotalNum);


        medicalAmount.setMedicalTotal(decimalinHospitalTotalNum.add(decimaloutpatientTotal));
        return medicalAmount;
    }


    @Override
    public PdByPIdDto getPdByPId(PdCondition condition) throws SQLException {
        int pageNum = condition.getPageNum() == null ? 1 : condition.getPageNum();

        int pageSize = 10;
        if (null != condition.getPageSize() && condition.getPageSize() != 0) {
            pageSize = condition.getPageSize();
        }
        PdByPIdDto pdByPIdDto = new PdByPIdDto();
        PageHelper.startPage(pageNum, pageSize);
        List<PaymentDetailsBean> paymentDetailsBeans = paymentDetailsDao.getPdByPId(condition);
        BigDecimal marginUseMax = BigDecimal.ZERO;
        BigDecimal patientUseMax = BigDecimal.ZERO;
        BigDecimal hospitalUseMax = BigDecimal.ZERO;
        if (!CollectionUtil.isEmpty(paymentDetailsBeans)) {
            for (PaymentDetailsBean paymentDetailsBean : paymentDetailsBeans) {
                double marginUse = paymentDetailsBean.getMarginUse() == null ? 0 : paymentDetailsBean.getMarginUse();
                double patientUse = paymentDetailsBean.getPatientUse() == null ? 0 : paymentDetailsBean.getPatientUse();
                double hospitalUse = paymentDetailsBean.getHospitalUse() == null ? 0 : paymentDetailsBean.getHospitalUse();
                BigDecimal margin = new BigDecimal(String.valueOf(marginUse));
                BigDecimal patient = new BigDecimal(String.valueOf(patientUse));
                BigDecimal hospital = new BigDecimal(String.valueOf(hospitalUse));
                if (paymentDetailsBean.getMarginType() == 2) {
                    margin = margin.multiply(new BigDecimal("-1"));
                    patient = patient.multiply(new BigDecimal("-1"));
                    hospital = hospital.multiply(new BigDecimal("-1"));
                }
                marginUseMax = marginUseMax.add(margin);
                patientUseMax = patientUseMax.add(patient);
                hospitalUseMax = hospitalUseMax.add(hospital);
            }
        }
        PageInfo<PaymentDetailsBean> beanPageInfo = new PageInfo<>(paymentDetailsBeans);
        pdByPIdDto.setPdByPId(beanPageInfo);
        pdByPIdDto.setMarginUseMax(marginUseMax);
        pdByPIdDto.setPatientUseMax(patientUseMax);
        pdByPIdDto.setHospitalUseMax(hospitalUseMax);
        return pdByPIdDto;
    }

   /* @Override
    public PdByPIdDto getPdByPId(PdCondition condition) throws SQLException, ParseException {
        int pageNum = condition.getPageNum() == null ? 1 : condition.getPageNum();

        int pageSize = 10;
        if (null != condition.getPageSize() && condition.getPageSize() != 0 ) {
            pageSize = condition.getPageSize();
        }

        // 清空非医疗临时表
        paymentDetailsDao.deleteAllNoMe();

        // 向临时表插入数据
        // 将服务计费表按照查询条件拆分成天
        // 1、确定开始时间 结束时间 同时查出限制条件内计费的项目
        List<NoMedicalBean> pbList = paPayserviceDao.getPBByCondition(condition);
        if (!CollectionUtil.isEmpty(pbList)) {
            // 医疗费明细表每天最新的一条数据
            List<PaymentDetailsBean> paymentDetailsBeanList = paymentDetailsDao.getNewestPDBEveryDay(condition.getIdCard());
            Map<String, List<PaymentDetailsBean>> pdMap = new HashMap<>();
            if (!CollectionUtil.isEmpty(paymentDetailsBeanList)) {
                Map<String, List<PaymentDetailsBean>> sourceMap = paymentDetailsBeanList
                        .stream()
                        .collect(Collectors.groupingBy(item -> DateUtils.formatDate(item.getPayDate())));
                Object[] array = sourceMap.keySet().toArray();
                Arrays.sort(array);
                for (int i = 0; i < array.length; i++) {
                    pdMap.put(String.valueOf(array[i]), sourceMap.get(String.valueOf(array[i])));
                }
            }

            NoMedicalBean min = pbList.stream().min(Comparator.comparing(NoMedicalBean::getBegDate)).get();
            Date minDate = DateUtils.parseDate(min.getBegDate());
            NoMedicalBean max = pbList.stream().max(Comparator.comparing(NoMedicalBean::getEndDate)).get();
            Date maxDate = DateUtils.parseDate(max.getEndDate());
            Date start = condition.getStartDate() == null ? minDate : condition.getStartDate();
            Date end = condition.getEndDate() == null ? maxDate : condition.getEndDate();
            // 2、根据时间段查出日期列表
            String idCard1 = condition.getIdCard();
            Integer integer1 = paPayserviceDao.selectOpen(idCard1);
            if (integer1 > 0) {
                end = new Date();
            }

            List<String> dateTable = paPayserviceDao.getDateTable(start, end);
            List<NoMedicalBean> countList = new ArrayList<>();
            // 3、同时遍历项目 日期列表 将服务计费表按照查询条件拆分成天
            for (NoMedicalBean noMedicalBean : pbList) {
                Date s = DateUtils.parseDate(noMedicalBean.getBegDate());
                Date ss = DateUtils.parseDate(noMedicalBean.getEndDate());
                for (String df : dateTable) {
                    NoMedicalBean medical = new NoMedicalBean();
                    // 该患者 当天 某个项目的计费实体
                    String concat = df.concat(" 23:59:59");
                    Date date = DateUtils.parseDate(concat);
                    if (date.compareTo(s) < 0 && ss.compareTo(date) < 0)
                        continue;
                    medical.setId(noMedicalBean.getId());
                    medical.setNoMedicalMoney(noMedicalBean.getUnitPrice());
                    medical.setPayDate(date);
                    medical.setPayDateFormat(concat);
                    medical.setIsHosp(noMedicalBean.getIsHosp());
                    medical.setDoctor(noMedicalBean.getDoctor());
                    medical.setDeptName(noMedicalBean.getDeptName());
                    medical.setBegDate(noMedicalBean.getBegDate());
                    medical.setEndDate(noMedicalBean.getEndDate());
                    medical.setDeptId(noMedicalBean.getDeptId());
                    medical.setHospNum(noMedicalBean.getHospNum());
                    medical.setIdCard(noMedicalBean.getIdCard());
                    medical.setPaName(noMedicalBean.getPaName());
                    medical.setUnitPrice(noMedicalBean.getUnitPrice());
                    countList.add(medical);
                }
            }

            for (int i = 0; i < countList.size(); i++) {
                String payDateFormat = countList.get(i).getPayDateFormat();
                String begDate = countList.get(i).getBegDate();
                String endDate = countList.get(i).getEndDate();
                Date date1 = DateUtils.parseDate(payDateFormat);
                Date date2 = DateUtils.parseDate(begDate);
                Date date3 = DateUtils.parseDate(endDate);
                Double v = date1.getTime() - 86400000D;

                if (date1.getTime() < date2.getTime()||date3.getTime()<v) {
                    countList.remove(i);
                    i--;
                }
            }



            // 4、将得到的数据 按缴费日期【天】+ 科室 汇总
            Map<String, List<NoMedicalBean>> noMap = countList.stream().collect(Collectors.groupingBy(item -> item.getPayDateFormat() + "|" + item.getDeptId()));
            List<NoMedicalBean> resultList = new ArrayList<>();

            Map<String, List<NoMedicalBean>> noMaps = new HashMap<>();
            for (String key : noMap.keySet()) {

                String[] split = key.split("\\|");


                String dateFormat = split[0];
                List<NoMedicalBean> noMedicalBeans = noMap.get(key);
                List<NoMedicalBean> noMedicalBeanList = new ArrayList<>();

                for (NoMedicalBean noMedicalBean : noMedicalBeans) {
                    String begDate = noMedicalBean.getBegDate();
                    String endDate = noMedicalBean.getEndDate();
                    Long begDateTamp = getTimestamp(begDate);
                    Long endDateTamp = getTimestamp(endDate);
                    Long dateFormatTamp = getTimestamp(dateFormat);
                    Integer integer = paPayserviceDao.selectType(noMedicalBean.getId());
                    if (begDateTamp <= dateFormatTamp && endDateTamp >= dateFormatTamp) {
                        //若开始时间<=选择时间<=结束时间
                        noMedicalBeanList.add(noMedicalBean);
                    } else if (integer == null) {
                        continue;
                    } else if (integer == 0) {
                        noMedicalBeanList.add(noMedicalBean);
                    }
                }
                noMaps.put(dateFormat, noMedicalBeanList);


/////////////////////////////////////////////////////////////////////////////////////////////
                *//*Date date = DateUtils.parseDate(dateFormat);
                NoMedicalBean result = new NoMedicalBean();
                List<NoMedicalBean> medicalBeans = noMap.get(key);
                NoMedicalBean bean = medicalBeans.get(0);

                BigDecimal sum = medicalBeans.stream().map(NoMedicalBean::getNoMedicalMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                BeanUtils.copyProperties(bean, result);
                result.setNoMedicalMoney(sum);
                // 没有押金
                if (CollectionUtil.isEmpty(pdMap)) {
                    result.setBalance(sum.multiply(new BigDecimal("-1")));
                    resultList.add(result);
                    continue;
                }

                List<PaymentDetailsBean> detailsBeans = pdMap.get(split[0]);

                // 这一天有医疗费记录
                if (!CollectionUtil.isEmpty(detailsBeans)) {
                    arrangeData(resultList, result, sum, detailsBeans.get(0));
                    continue;
                }

                // 若没有医疗费记录
                // 发现前一天有非医疗缴费【先查非医疗缴费原因是 每天的数据 非医疗缴费永远是最后一条，这样算出的余额才是对的】
                NoMedicalBean minBean = null;
                if (!CollectionUtil.isEmpty(resultList)) {
                    List<NoMedicalBean> beans = resultList.stream().filter(item -> date.compareTo(item.getPayDate()) >= 0).collect(Collectors.toList());
                    if (!CollectionUtil.isEmpty(beans)) {
                        minBean = beans.stream().min(Comparator.comparing(NoMedicalBean::getPayDate)).get();
                    }
                }
                List<PaymentDetailsBean> pdBeanList = new ArrayList<>();
                for (String pdKey : pdMap.keySet()) {
                    Date parseDate = DateUtils.parseDate(pdKey);
                    if (date.compareTo(parseDate) >= 0) {
                        pdBeanList.add(pdMap.get(pdKey).get(0));
                    }
                }

                if (!CollectionUtil.isEmpty(pdBeanList)) {
                    PaymentDetailsBean detailsBean = pdBeanList.stream()
                            .min(Comparator.comparing(PaymentDetailsBean::getPayDate)).get();

                    if (null != minBean && detailsBean.getPayDate().compareTo(minBean.getPayDate()) >= 0) {
                        result.setIsHosp(minBean.getIsHosp());
                        BigDecimal subtract = new BigDecimal(String.valueOf(minBean.getBalance())).subtract(sum);
                        result.setBalance(subtract);
                        resultList.add(result);
                    } else {
                        arrangeData(resultList, result, sum, detailsBean);
                    }
                } else {
                    if (null != minBean) {
                        result.setIsHosp(minBean.getIsHosp());
                        BigDecimal subtract = new BigDecimal(String.valueOf(minBean.getBalance())).subtract(sum);
                        result.setBalance(subtract);
                    } else {
                        result.setBalance(sum.multiply(new BigDecimal("-1")));
                    }
                    resultList.add(result);
                }*//*
            }
            List<NoMedicalBean> noMedicalBeanLists = new ArrayList<>();
            for (String key : noMaps.keySet()) {
                BigDecimal maxx = new BigDecimal("0");
                String idCard = null;
                String hospNum = null;
                String paName = null;
                String deptName = null;
                Integer deptId = null;
                Integer isHosp = null;
                String doctor = null;


                List<NoMedicalBean> noMedicalBeanList = noMaps.get(key);
                for (int i = 0; i < noMedicalBeanList.size(); i++) {
                    String begDate = noMedicalBeanList.get(i).getBegDate();
                    String endDate = noMedicalBeanList.get(i).getEndDate();
                    BigDecimal unitPrice = noMedicalBeanList.get(i).getUnitPrice();
                    maxx = maxx.add(unitPrice);
                    Long id = noMedicalBeanList.get(i).getId();

                    String idCards = noMedicalBeanList.get(noMedicalBeanList.size() - 1).getIdCard();
                    idCard = idCards;
                    String hospNums = noMedicalBeanList.get(noMedicalBeanList.size() - 1).getHospNum();
                    hospNum = hospNums;
                    String paNames = noMedicalBeanList.get(noMedicalBeanList.size() - 1).getPaName();
                    paName = paNames;
                    String deptNames = noMedicalBeanList.get(noMedicalBeanList.size() - 1).getDeptName();
                    deptName = deptNames;
                    Integer deptIds = noMedicalBeanList.get(noMedicalBeanList.size() - 1).getDeptId();
                    deptId = deptIds;

                    isHosp = noMedicalBeanList.get(noMedicalBeanList.size() - 1).getIsHosp();

                    String doctors = noMedicalBeanList.get(noMedicalBeanList.size() - 1).getDoctor();
                    doctor = doctors;
                }
                if (isHosp == null) {
                    isHosp = paymentDetailsDao.selectisHosp(hospNum);
                }
                NoMedicalBean noMedicalBean = new NoMedicalBean();
                noMedicalBean.setIdCard(idCard);
                noMedicalBean.setPayDateFormat(key);
                noMedicalBean.setNoMedicalMoney(maxx);
                noMedicalBean.setHospNum(hospNum);
                noMedicalBean.setPaName(paName);
                noMedicalBean.setDeptName(deptName);
                noMedicalBean.setIsHosp(isHosp);
                noMedicalBean.setDoctor(doctor);
                noMedicalBean.setDeptId(deptId);

                noMedicalBeanLists.add(noMedicalBean);


            }
            insertBatch(noMedicalBeanLists);
            *//*if (!CollectionUtil.isEmpty(resultList)) {
                insertBatch(resultList);
            }*//*
        }

        // 查询明细结果



        List<PaymentDetailsBean> pdByPId = paymentDetailsDao.getPdByPId(condition);

*//*

     *//*
        for (int i = 0; i < pdByPId.size(); i++) {
            PaymentDetailsBean paymentDetailsBean = pdByPId.get(i);
            Integer marginType = paymentDetailsBean.getMarginType();//1添加2退费
            Double marginUse = paymentDetailsBean.getMarginUse();//押金发生
            Double hospitalUse = paymentDetailsBean.getHospitalUse();//住院发生
            Double patientUse = paymentDetailsBean.getPatientUse();//门诊发生
            BigDecimal noMeUse = paymentDetailsBean.getNoMeUse();//非医疗发生
           *//* if ((marginUse==null || marginUse==0)&&(hospitalUse==null || hospitalUse==0)&&(patientUse==null || patientUse==0)&&(noMeUse==null || noMeUse.compareTo(new BigDecimal(0))==0)){
                pdByPId.remove(i);
                i--;
            }*//*
            //假如是第一条数据
            if (i == 0) {
                PaymentDetailsBean paymentDetailsBean0 = pdByPId.get(i);
                Integer marginType0 = paymentDetailsBean0.getMarginType();//1添加2退费
                Double marginUse0 = paymentDetailsBean0.getMarginUse();//押金发生
                Double hospitalUse0 = paymentDetailsBean0.getHospitalUse();//住院发生
                Double patientUse0 = paymentDetailsBean0.getPatientUse();//门诊发生
                BigDecimal noMeUse0 = paymentDetailsBean0.getNoMeUse();//非医疗发生
                if (marginUse0 != null) {
                    if (marginType0 == 1) {
                        BigDecimal bg = new BigDecimal(marginUse0);
                        marginUse0 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean0.setCurrentMargin(marginUse0);
                    } else {
                        BigDecimal bg = new BigDecimal(marginUse0 * -1);
                        marginUse0 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean0.setCurrentMargin(marginUse0);
                    }
                }
                if (hospitalUse0 != null) {
                    if (marginType0 != 1) {
                        BigDecimal bg = new BigDecimal(hospitalUse0);
                        hospitalUse0 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean0.setCurrentMargin(hospitalUse0);
                    } else {
                        BigDecimal bg = new BigDecimal(hospitalUse0 * -1);
                        hospitalUse0 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean0.setCurrentMargin(hospitalUse0);
                    }
                }
                if (patientUse0 != null) {
                    if (marginType0 != 1) {
                        BigDecimal bg = new BigDecimal(patientUse0);
                        patientUse0 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean0.setCurrentMargin(patientUse0);
                    } else {
                        BigDecimal bg = new BigDecimal(patientUse0 * -1);
                        patientUse0 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean0.setCurrentMargin(patientUse0);
                    }
                }
                if (noMeUse0 != null) {
                    BigDecimal multiply = noMeUse0.multiply(new BigDecimal(-1));
                    double rs = multiply.doubleValue();
                    BigDecimal bg = new BigDecimal(rs);
                    rs = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    paymentDetailsBean0.setCurrentMargin(rs);
                }
            } else {
                if (marginUse != null) {
                    if (marginType == 1) {
                        Double marginAmount = pdByPId.get(i - 1).getCurrentMargin();
                        double var2 = Double.valueOf(marginAmount) + marginUse;

                        BigDecimal bg = new BigDecimal(var2);
                        var2 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();


                        paymentDetailsBean.setCurrentMargin(var2);
                    } else {
                        Double marginAmount = pdByPId.get(i - 1).getCurrentMargin();
                        double var2 = Double.valueOf(marginAmount) + (marginUse * -1);

                        BigDecimal bg = new BigDecimal(var2);
                        var2 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();


                        paymentDetailsBean.setCurrentMargin(var2);
                    }
                }
                if (hospitalUse != null) {
                    if (marginType != 1) {
                        Double marginAmount = pdByPId.get(i - 1).getCurrentMargin();
                        double var2 = Double.valueOf(marginAmount) + hospitalUse;

                        BigDecimal bg = new BigDecimal(var2);
                        var2 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean.setCurrentMargin(var2);
                    } else {
                        Double marginAmount = pdByPId.get(i - 1).getCurrentMargin();
                        double var2 = Double.valueOf(marginAmount) + (hospitalUse * -1);
                        BigDecimal bg = new BigDecimal(var2);
                        var2 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean.setCurrentMargin(var2);
                    }
                }
                if (patientUse != null) {
                    if (marginType != 1) {
                        Double marginAmount = pdByPId.get(i - 1).getCurrentMargin();
                        double var2 = Double.valueOf(marginAmount) + patientUse;
                        BigDecimal bg = new BigDecimal(var2);
                        var2 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean.setCurrentMargin(var2);
                    } else {
                        Double marginAmount = pdByPId.get(i - 1).getCurrentMargin();
                        double var2 = Double.valueOf(marginAmount) + (patientUse * -1);
                        BigDecimal bg = new BigDecimal(var2);
                        var2 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        paymentDetailsBean.setCurrentMargin(var2);
                    }
                }
                if (noMeUse != null) {
                    Double marginAmount = pdByPId.get(i - 1).getCurrentMargin();
                    BigDecimal bigDecimal = new BigDecimal(marginAmount);
                    BigDecimal multiply = noMeUse.multiply(new BigDecimal("-1"));
                    BigDecimal add = bigDecimal.add(multiply);
                    double rs = add.doubleValue();
                    BigDecimal bg = new BigDecimal(rs);
                    rs = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    paymentDetailsBean.setCurrentMargin(rs);
                }
            }
        }
        //List<List<PaymentDetailsBean>> lists = splitList(pdByPId, pageSize);
        Integer sumTotal = pdByPId.size();
        List list = listToPage(pageNum, pageSize, pdByPId);
        PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);

        //查询合计
        Double marginUseMax = 0D;
        Double patientUseMax = 0D;
        Double hospitalUseMax = 0D;
        BigDecimal noMeUseMax = new BigDecimal("0");
        for (PaymentDetailsBean paymentDetailsBean : pdByPId) {
            Double marginUse = paymentDetailsBean.getMarginUse();//押金发生
            Double patientUse = paymentDetailsBean.getPatientUse();//门诊发生
            Double hospitalUse = paymentDetailsBean.getHospitalUse();//住院发生
            BigDecimal noMeUse = paymentDetailsBean.getNoMeUse();//非医疗发生
            if (marginUse == null) {
                marginUse = 0D;
            }
            if (patientUse == null) {
                patientUse = 0D;
            }
            if (hospitalUse == null) {
                hospitalUse = 0D;
            }
            if (noMeUse == null) {
                noMeUse = new BigDecimal("0");
            }
            if (paymentDetailsBean.getMarginType() == 1) {
                marginUseMax = marginUseMax + marginUse;
                patientUseMax = patientUse + patientUseMax;
                hospitalUseMax = hospitalUseMax + hospitalUse;
                noMeUseMax = noMeUseMax.add(noMeUse);
            } else {
                marginUseMax = marginUseMax + marginUse * -1;
                patientUseMax = patientUse + patientUseMax * -1;
                hospitalUseMax = hospitalUseMax + hospitalUse * -1;
                noMeUseMax = noMeUseMax.add(noMeUse.multiply(new BigDecimal("-1")));
            }

        }
        marginUseMax = (double) Math.round(marginUseMax * 100) / 100;
        patientUseMax = (double) Math.round(patientUseMax * 100) / 100;
        hospitalUseMax = (double) Math.round(hospitalUseMax * 100) / 100;
        if (marginUseMax < 0) {
            marginUseMax = marginUseMax * -1;
        }
        if (patientUseMax < 0) {
            patientUseMax = patientUseMax * -1;
        }
        if (hospitalUseMax < 0) {
            hospitalUseMax = hospitalUseMax * -1;
        }


        PdByPIdDto pdByPIdDto = new PdByPIdDto();
        pdByPIdDto.setPdByPId(result);
        pdByPIdDto.setMarginUseMax(marginUseMax);
        pdByPIdDto.setPatientUseMax(patientUseMax);
        pdByPIdDto.setHospitalUseMax(hospitalUseMax);
        pdByPIdDto.setNoMeUseMax(noMeUseMax);
        pdByPIdDto.setSumTotal(sumTotal);
        return pdByPIdDto;
    }*/

    private void countUse(List<PaymentDetailsBean> pdByPId, int i, PaymentDetailsBean paymentDetailsBean, Integer marginType, BigDecimal hospitalUse) {
        if (hospitalUse != null) {
            BigDecimal marginAmount = new BigDecimal(String.valueOf(pdByPId.get(i - 1).getCurrentMargin()));
            if (marginType != 1) {
                paymentDetailsBean.setCurrentMargin(marginAmount.add(hospitalUse).doubleValue());
            } else {
                BigDecimal multiply = hospitalUse.multiply(new BigDecimal("-1"));
                paymentDetailsBean.setCurrentMargin(marginAmount.add(new BigDecimal(String.valueOf(multiply))).doubleValue());
            }
        }


    }

    public List listToPage(int pageNum, int pageSize, List list) {
        if (CollectionUtil.isEmpty(list)) {
            return new ArrayList();
        }
        int limit = (pageNum - 1) * pageSize;
        int size = list.size();
        int totalPage = getTotalPage(pageSize, size);
        if (pageNum > totalPage) {
            throw new RuntimeException("页数超出了");
        }
        List<Integer> subList = null;
        if (pageNum == totalPage) {
            System.out.println("最后一页");
            subList = list.subList(limit, size);
        } else {
            int end = limit + pageSize;
            System.out.println("截取的最后的下标 = " + end);
            subList = list.subList(limit, end);
        }
        return subList;
    }


    /**
     * 根据总条数获取总页数
     *
     * @param pageSize
     * @param totalSize
     * @return
     */
    public int getTotalPage(int pageSize, int totalSize) {
        int totalPage = 0;
        if (totalSize % pageSize == 0) {
            totalPage = totalSize / pageSize;
        } else {
            totalPage = totalSize / pageSize + 1;
        }
        return totalPage;
    }


    private void arrangeData(List<NoMedicalBean> resultList, NoMedicalBean result, BigDecimal sum, PaymentDetailsBean detailsBean) {
        result.setIsHosp(detailsBean.getIsInHospital());
        BigDecimal subtract = new BigDecimal(String.valueOf(detailsBean.getCurrentMargin())).subtract(sum);
        result.setBalance(subtract);
        resultList.add(result);
    }

    /*


        String hospNum = hospitalPatientCondition.getHospNum();
        List<PaPayserviceBean> paPayserviceBeans = paymentDetailsDao.findAllTime(hospNum);
        //Map<String, List<PaPayserviceBean>> collect = paPayserviceBeans.stream().collect(Collectors.groupingBy(PaPayserviceBean::getHospNum));

        Map<String, List<BigDecimal>> map = new HashMap<>();
                for (PaPayserviceBean paPayserviceBean : paPayserviceBeans) {
            String preReceive = paPayserviceBean.getPreReceive();
            BigDecimal b =new BigDecimal(preReceive);

            String begDates = paPayserviceBean.getBegDate();
            String endDates = paPayserviceBean.getEndDate();
            List<String> allday = paymentDetailsDao.findAllday(begDates, endDates);
            BigDecimal number = new BigDecimal(0);
            int value=allday.size();
            number=BigDecimal.valueOf((int)value);
            BigDecimal result5 = b.divide(number,2,BigDecimal.ROUND_HALF_UP);
            for (String day : allday) {
                if (map.containsKey(day)) {//map有这一天
                    List<BigDecimal> integers = map.get(day);
                    integers.add(result5);
                } else {
                    List<BigDecimal> listInt = new ArrayList<>();
                    listInt.add(result5);
                    map.put(day, listInt);
                }
            }
        }
        //至此把时间跟余额集合存到了map里面
        //放完map
                for (PaymentDetailsBean paymentDetailsBean : list) {
            Integer pdId = paymentDetailsBean.getPdId();
            String patientName = paymentDetailsBean.getPatientName();
            String deptName = paymentDetailsBean.getDeptName();
            Integer isInHospital = paymentDetailsBean.getIsInHospital();
        }
                for (String key : map.keySet()) {//keySet获取map集合key的集合  然后在遍历key即可
            for (PaPayserviceBean paPayserviceBean : paPayserviceBeans) {
                List<BigDecimal> integers = map.get(key);
                BigDecimal number = new BigDecimal(0);
                for (BigDecimal integer : integers) {
                    number = integer.add(number);
                }
                PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
                paymentDetailsBean.setNoMeUse(number);
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime ldt = LocalDateTime.parse(key,df);
                paymentDetailsBean.setCreateDate(ldt);
                paymentDetailsBean.setHospNum(hospNum);
            }
        }

                //一期先这么改，假如后期需要减一天就注释一下代码，从这行起一直到if以上
            Date endDate = hospitalPatientCondition.getEndDate();
            if (endDate != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(endDate);
                c.add(Calendar.DAY_OF_MONTH, 1);
                Date time = c.getTime();
                hospitalPatientCondition.setEndDate(time);
            }
    */
    public static List<List<PaymentDetailsBean>> splitList(List<PaymentDetailsBean> list, int pageSize) { //集合数据，分页尺寸

        int listSize = list.size();
        int page = (listSize + (pageSize - 1)) / pageSize;
        List<List<PaymentDetailsBean>> listArray = new ArrayList<List<PaymentDetailsBean>>();
        for (int i = 0; i < page; i++) {
            List<PaymentDetailsBean> subList = new ArrayList<PaymentDetailsBean>();
            for (int j = 0; j < listSize; j++) {
                int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize;
                if (pageIndex == (i + 1)) {
                    subList.add(list.get(j));
                }
                if ((j + 1) == ((j + 1) * pageSize)) {
                    break;
                }
            }
            listArray.add(subList);
        }
        return listArray;
    }

    public void insertBatch(List<NoMedicalBean> medicalBeanList) throws SQLException {
        final String sql = "INSERT INTO no_medical (id_card,pay_date,no_medical_money,hosp_num,pa_name,dept_name,is_hosp,doctor,balance,dept_id) values (?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            // 获取数据库连接
            conn = ds.getConnection();
            if (conn == null) {
                throw (new RuntimeException("获取数据库连接失败"));
            }
            // 预编译SQL
            ps = conn.prepareStatement(sql);
            // 关闭自动提交事务
            conn.setAutoCommit(false);
            for (NoMedicalBean medicalBean : medicalBeanList) {
                ps.setString(1, medicalBean.getIdCard());
                ps.setString(2, medicalBean.getPayDateFormat());
                ps.setBigDecimal(3, medicalBean.getNoMedicalMoney());
                ps.setString(4, medicalBean.getHospNum());
                ps.setString(5, medicalBean.getPaName());
                ps.setString(6, medicalBean.getDeptName());
                if (null != medicalBean.getIsHosp()) {
                    ps.setInt(7, medicalBean.getIsHosp());
                } else {
                    ps.setInt(7, 2);
                }

                ps.setString(8, medicalBean.getDoctor());
                ps.setBigDecimal(9, medicalBean.getBalance());
                if (null != medicalBean.getDeptId()) {
                    ps.setInt(10, medicalBean.getDeptId());
                } else {
                    ps.setInt(10, 0);
                }

                ps.addBatch();
            }
            // 执行批量入库
            ps.executeBatch();
            // 手动提交事务
            conn.commit();

        } catch (Exception e) {
            // 批量入库异常，回滚
            e.printStackTrace();
            conn.rollback();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    public Long getTimestamp(String time) {
        Long timestamp = null;
        try {
            timestamp = new SimpleDateFormat("yyyy-MM-dd").parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}
