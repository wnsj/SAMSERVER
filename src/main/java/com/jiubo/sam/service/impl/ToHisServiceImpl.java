package com.jiubo.sam.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.*;
import com.jiubo.sam.dto.*;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.schedule.ToHisTask;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.service.PatientService;
import com.jiubo.sam.service.ToHisService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.TimeUtil;
import com.jiubo.sam.util.WebApiUtil;
import freemarker.template.utility.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ToHisServiceImpl implements ToHisService {

//    private static final String url = "http://192.168.10.2:8081/WebService_Sam_Hospital.asmx?wsdl";

    @Autowired
    private ToHisDao toHisDao;

    @Autowired
    private HospitalPatientService hospitalPatientService;

    @Autowired
    private PaymentDetailsDao paymentDetailsDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private ToHisTask toHisTask;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private PatinetMarginDao patinetMarginDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addHisEmp(String param) throws MessageException {
        JSONObject jsonObject = JSONObject.parseObject(param);

        String hospNum = jsonObject.getString("hospNum");
        String name = jsonObject.getString("name");
        String identityCard = jsonObject.getString("identityCard");
        String sex = jsonObject.getString("sex");
        String age = jsonObject.getString("age");
        String deptId = jsonObject.getString("deptId");
        String mitypeid = jsonObject.getString("mitypeid");
        String inPatientAreaNo = jsonObject.getString("InPatientAreaNo");
        String hospDateStr = jsonObject.getString("hospDate");
        // 判空
        judgeEmpty(hospNum, name, identityCard, deptId, mitypeid, hospDateStr, inPatientAreaNo);
        Date hospDate = DateUtils.parseDate(hospDateStr);
        List<PatientBean> allIdCard = patientDao.getAllIdCard();
        Date date = new Date();
        List<DepartmentBean> allDeptCode = departmentDao.getAllDeptCode();
        Map<String, List<DepartmentBean>> map = null;
        if (!CollectionUtils.isEmpty(allDeptCode)) {
            map = allDeptCode.stream().collect(Collectors.groupingBy(DepartmentBean::getDeptCode));
        }
        String code = null;
        if (null != map) {
            List<DepartmentBean> departmentBeans = map.get(deptId);
            if (!CollectionUtils.isEmpty(departmentBeans)) {
                code = departmentBeans.get(0).getDeptId();
            }
        }
//        String num = "H".concat(String.valueOf(date.getTime()));
        String num = null;
        if (!StringUtils.isEmpty(inPatientAreaNo)) {
            num = inPatientAreaNo;
        }
        PatientHiSDto patientHiSDto = PatientHiSDto.builder()
                .hospNum(num).name(name).creator(99999)
                .hisWaterNum(hospNum)
                .hospDate(hospDate)
                .identityCard(identityCard).sex(sex)
                .age(age).deptId(code).mitypeid(mitypeid)
                .build();
        if (!CollectionUtils.isEmpty(allIdCard)) {
            List<String> list = allIdCard.stream().map(PatientBean::getIdCard).collect(Collectors.toList());
            if (!list.contains(identityCard)) {
                return toHisDao.addHisPatient(patientHiSDto);
            } else if (!StringUtils.isEmpty(hospNum)) {
                // 更新his流水号
                return toHisDao.patchHisWaterNum(hospNum,identityCard);
            }
        } else {
            return toHisDao.addHisPatient(patientHiSDto);
        }
        return 0;
    }

    private void judgeEmpty(String hospNum, String name,
                            String identityCard,
                            String deptId,
                            String mitypeid,
                            String hospDate,
                            String inPatientAreaNo) throws MessageException {
//        if (StringUtils.isEmpty(hospNum)) {
//            throw new MessageException("住院号不可为空");
//        }
        if (StringUtils.isEmpty(name)) {
            throw new MessageException("患者名字不可为空");
        }
        if (StringUtils.isEmpty(identityCard)) {
            throw new MessageException("身份证号不可为空");
        }
        if (StringUtils.isEmpty(deptId)) {
            throw new MessageException("科室id不可为空");
        }
        if (StringUtils.isEmpty(mitypeid)) {
            throw new MessageException("医保类型不可为空");
        }
        if (StringUtils.isEmpty(hospDate)) {
            throw new MessageException("入院时间不可为空");
        }

//        if (StringUtils.isEmpty(inPatientAreaNo)) {
//            throw new MessageException("病案号不可为空");
//        }
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject refundOrAddHP(String param) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(param);
        String hisLowNum = jsonObject.getString("hisLowNum");
        String hospNum = jsonObject.getString("hospNum");
        String identityCard = jsonObject.getString("identityCard");
        Integer consumType = jsonObject.getInteger("consumType");
        String deptId = jsonObject.getString("deptId");
        String empId = jsonObject.getString("empId");
        String nowDate = jsonObject.getString("nowDate");
        BigDecimal realCross = jsonObject.getBigDecimal("realCross");
        Integer type = jsonObject.getInteger("type");

        // 对必填字段判空
        judgeIsEmpty(hospNum, identityCard, consumType, nowDate, realCross, type);

        String replaceAll = nowDate.replaceAll("\\/", "-");
        // 判断余额是否充足
//        judgeBalance(identityCard);

        List<DepartmentBean> allDeptCode = departmentDao.getAllDeptCode();
        Map<String, List<DepartmentBean>> map = null;
        if (!CollectionUtils.isEmpty(allDeptCode)) {
            map = allDeptCode.stream().collect(Collectors.groupingBy(DepartmentBean::getDeptCode));
        }

        List<EmployeeBean> allPerCode = employeeDao.getAllPerCode();
        Map<String, List<EmployeeBean>> pMap = null;
        if (!CollectionUtils.isEmpty(allPerCode)) {
            pMap = allPerCode.stream().collect(Collectors.groupingBy(EmployeeBean::getPerCode));
        }

        List<PatientBean> patientBeans = toHisDao.accurateQuery(identityCard);

        if (CollectionUtil.isEmpty(patientBeans)) {
            throw new MessageException("Sam没有该患者");
        }
        PatientBean patientBean = patientBeans.get(0);

        HospitalPatientBean hospitalPatientBean = new HospitalPatientBean();
        hospitalPatientBean.setIdCard(identityCard);
        hospitalPatientBean.setSerialNumberHis(hisLowNum);
        hospitalPatientBean.setRealCross(realCross.doubleValue());
        hospitalPatientBean.setHospNum(patientBean.getHospNum());
        hospitalPatientBean.setConsumType(consumType);


        Date date = TimeUtil.parseDateYYYY_MM_DD_HH_MM_SS(replaceAll);
        hospitalPatientBean.setPayDate(date);
        hospitalPatientBean.setIsInHospital(Integer.parseInt(patientBean.getInHosp()));
        hospitalPatientBean.setAccountId(99999);
        hospitalPatientBean.setType(type);
        if (!StringUtils.isEmpty(deptId)) {
            if (null != map) {
                List<DepartmentBean> departmentBeans = map.get(deptId);
                if (!CollectionUtils.isEmpty(departmentBeans)) {
                    hospitalPatientBean.setDeptId(Integer.parseInt(departmentBeans.get(0).getDeptId()));
                }
            }
        } else {
            hospitalPatientBean.setDeptId(Integer.parseInt(patientBean.getDeptId()));
        }
        if (null != empId) {
            if (null != pMap) {
                List<EmployeeBean> employeeBeans = pMap.get(empId);
                if (!CollectionUtil.isEmpty(employeeBeans)) {
                    EmployeeBean employeeBean = employeeBeans.get(0);
                    hospitalPatientBean.setEmpId(Integer.parseInt(String.valueOf(employeeBean.getId())));
                }
            }
        } else {
            hospitalPatientBean.setEmpId(Integer.parseInt(patientBean.getEmpId()));
        }
        LocalDateTime dateTime = LocalDateTime.now();
        hospitalPatientBean.setCreateDate(dateTime);
        String serialNumber;
        if (consumType == 1) {
            serialNumber = hospitalPatientService.addHospitalPatient(hospitalPatientBean);
        } else {
            serialNumber = hospitalPatientService.refundHospitalPatient(hospitalPatientBean);
        }
        JSONObject returnData = new JSONObject();
        returnData.put("samLowNum", serialNumber);
        returnData.put("hisLowNum", hisLowNum);
        return returnData;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addFee(String idCard) throws Exception {
        if (StringUtils.isEmpty(idCard)) {
            throw new MessageException("身份证号空");
        }
        PatientBean patientBean = patientDao.getPatientByIdCard(idCard);
        if (null == patientBean) {
            throw new MessageException("沒有患者");
        }

        if (StringUtils.isEmpty(patientBean.getHisWaterNum())) {
            throw new MessageException("HIS流水号为空,请确认该患者在HIS是否入科");
        }

        // 判断余额是否充足
//        judgeBalance(idCard);

        HospitalPatientBean hospitalPatientBean = new HospitalPatientBean();
        Date date = new Date();
        LocalDateTime dateTime = LocalDateTime.now();
        hospitalPatientBean.setPayDate(date);
        hospitalPatientBean.setIdCard(patientBean.getIdCard());
        hospitalPatientBean.setCreateDate(dateTime);
        hospitalPatientBean.setHospNum(patientBean.getHospNum());
        hospitalPatientBean.setDeptId(Integer.parseInt(patientBean.getDeptId()));
        BigDecimal bigDecimal = new BigDecimal("3000");
        hospitalPatientBean.setRealCross(bigDecimal.doubleValue());
        hospitalPatientBean.setAccountId(88888);
        hospitalPatientBean.setHisWaterNum(patientBean.getHisWaterNum());
        hospitalPatientBean.setType(1);
        hospitalPatientBean.setUpdateDate(date);
        hospitalPatientBean.setConsumType(1);
        hospitalPatientBean.setIsInHospital(Integer.parseInt(patientBean.getInHosp()));
        String serialNumber = hospitalPatientService.addHospitalPatient(hospitalPatientBean);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("VisitSn", hospitalPatientBean.getHisWaterNum());
        String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        jsonObject.put("TranDate", formatDate);
        jsonObject.put("Cusid", hospitalPatientBean.getIdCard());
        jsonObject.put("TradeNo", serialNumber);
        jsonObject.put("PayType", "9943");
        jsonObject.put("Amt", "3000");

        Object[] z003s = requestHis("Z003", jsonObject.toJSONString());
//        String s = WebApiUtil.ReaderFileToString("D:\\/2021-07-11.txt");
//        JSONArray objects = JSONObject.parseArray(s);
        for (Object o : z003s) {
            JSONObject object = JSONObject.parseObject(String.valueOf(o));
            JSONObject message = object.getJSONObject("message");
            String code = message.getString("code");
            if (!code.equals("1")) {
                throw new MessageException("his拨款失败");
            }
        }
        return serialNumber;
    }

    private void judgeBalance(String idCard) throws MessageException {
        List<PatinetMarginBean> mByIdCard = patinetMarginDao.getMByIdCard(idCard);
        if (CollectionUtil.isEmpty(mByIdCard)) {
            throw new MessageException("余额不足请充值");
        }
        PatinetMarginBean marginBean = mByIdCard.get(0);
        Double money = marginBean.getMoney() == null ? 0 : marginBean.getMoney();
        BigDecimal decimal = new BigDecimal(String.valueOf(money));
        if (new BigDecimal("3000").compareTo(decimal) < 0) {
            throw new MessageException("余额不足3000请充值");
        }
    }

    public Object[] requestHis(String method, String param) {
        String url = toHisDao.getApiUrl();
        log.error("hisUrl:"+url);
        Object[] objs = new Object[2];
        objs[0] = method;
        objs[1] = param;
        Object[] result = WebApiUtil.execWebService(url, "CallWebMethod", objs);
        if (null == result || result.length <= 0) {
            return null;
        }
        return result;
    }

    private void judgeIsEmpty(String hospNum, String identityCard, Integer consumType, String nowDate, BigDecimal realCross, Integer type) throws MessageException {
        if (StringUtils.isEmpty(hospNum)) {
            throw new MessageException("住院号不能为空");
        }
        if (StringUtils.isEmpty(identityCard)) {
            throw new MessageException("身份证号不能为空");
        }
        if (null == consumType) {
            throw new MessageException("缴退类型不能为空");
        }
        if (StringUtils.isEmpty(nowDate)) {
            throw new MessageException("交易时间不能为空");
        }
        if (null == realCross || BigDecimal.ZERO.compareTo(realCross) >= 0) {
            throw new MessageException("发生金额不能为空");
        }
        if (null == type) {
            throw new MessageException("缴费类型不能为空");
        }
    }

    @Override
    public CaTableDto getCATable(CACondition condition) {
        String startDate = condition.getStartDate();
        String endDate = condition.getEndDate();

        int pageNum = condition.getPageNum() == null ? 1 : condition.getPageNum();
        int pageSize = condition.getPageSize() == null ? 10 : condition.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        // 圣安数据
        List<CheckAccount> caTable = paymentDetailsDao.getCATable(condition);
        // 获取his数据
        Map<String, JSONObject> hisDataMap = new HashMap<>();
        try {
            hisDataMap = getHisData(startDate, endDate);
        } catch (Exception e) {
            log.error("获取HIS数据异常");
        }

        // 医生数据
        List<EmployeeBean> employeeBeanList = employeeDao.getAllEmp();
        Map<Long, List<EmployeeBean>> empMap = null;
        if (!CollectionUtil.isEmpty(employeeBeanList)) {
            empMap = employeeBeanList.stream().collect(Collectors.groupingBy(EmployeeBean::getId));
        }

        if (CollectionUtil.isEmpty(caTable)) return null;

        // 整合圣安 HIS 数据【目前根据 身份证号+HIS的流水号进行匹配】
        for (CheckAccount checkAccount : caTable) {

            Date tradeDate = checkAccount.getSamTradeDate();
            if (null != tradeDate) {
                String date = DateUtils.formatDate(tradeDate, "yyyy-MM-dd HH:mm:ss");
                checkAccount.setSamTradeDateFormat(date);
            }
            if (hisDataMap.isEmpty()) continue;
            JSONObject hisObj = hisDataMap.get(checkAccount.getSamIdCard() + "|" + checkAccount.getSamSerialNumberSam());
            if (null == hisObj) continue;

            checkAccount.setHisCharge(hisObj.getBigDecimal("ysje"));
            checkAccount.setHisDeveloper(hisObj.getString("port"));
            checkAccount.setHisIdCard(hisObj.getString("Kh"));
            checkAccount.setHisPayMethod(hisObj.getString("PayType"));
            checkAccount.setHisPayType(hisObj.getInteger("zfzt"));
            checkAccount.setHisRefund(hisObj.getBigDecimal("tfje"));
            checkAccount.setHisSerialNumberHis(hisObj.getString("TradeNo"));
            checkAccount.setHisTradeDate(hisObj.getString("jyrq"));
            if (null == empMap) continue;
            List<EmployeeBean> employeeBeanList1 = empMap.get(hisObj.getLong("czyh"));
            if (!CollectionUtil.isEmpty(employeeBeanList1)) {
                checkAccount.setHisOperator(employeeBeanList1.get(0).getEmpName());
            }
        }

        //加上缴费，退费总计
        BigDecimal samChargeMax = new BigDecimal("0");//圣安缴费金额
        BigDecimal samRefundMax = new BigDecimal("0");//圣安退费金额
        BigDecimal hisChargeMax = new BigDecimal("0");//HIS缴费金额
        BigDecimal hisRefundMax = new BigDecimal("0");//HIS退费金额

        for (CheckAccount checkAccount : caTable) {
            BigDecimal samCharge = checkAccount.getSamCharge();
            BigDecimal samRefund = checkAccount.getSamRefund();
            BigDecimal hisCharge = checkAccount.getHisCharge() == null ? new BigDecimal("0") : checkAccount.getHisCharge();
            BigDecimal hisRefund = checkAccount.getHisRefund() == null ? new BigDecimal("0") : checkAccount.getHisRefund();
            samChargeMax = samChargeMax.add(samCharge);
            samRefundMax = samRefundMax.add(samRefund);
            hisChargeMax = hisChargeMax.add(hisCharge);
            hisRefundMax = hisRefundMax.add(hisRefund);
        }
        PageInfo<CheckAccount> result = new PageInfo<>(caTable);

        CaTableDto caTableDto = new CaTableDto();
        caTableDto.setList(result);
        caTableDto.setSamChargeMax(samChargeMax);
        caTableDto.setSamRefundMax(samRefundMax);
        caTableDto.setHisChargeMax(hisChargeMax);
        caTableDto.setHisRefundMax(hisRefundMax);
        return caTableDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importDefault() {
        // 状态为开启的默认计费启动项目
        List<PaPayserviceBean> openDefault = toHisDao.getOpenDefault();

        List<PaymentBean> newestByPAndS = toHisDao.getNewestByPAndS();

        Map<String, List<PaymentBean>> nMFeeMap = null;
        if (!CollectionUtil.isEmpty(newestByPAndS)) {
            nMFeeMap = newestByPAndS.stream().collect(Collectors.groupingBy(item -> item.getPatientId() + "|" + item.getPayserviceId()));
        }

        List<PaPayserviceBean> patchList = new ArrayList<>();
        List<PaPayserviceBean> addList = new ArrayList<>();
        for (PaPayserviceBean paPayserviceBean : openDefault) {
            String key = paPayserviceBean.getPatientId() + "|" + paPayserviceBean.getPayserviceId();

            // 所有患者都没交过钱
            if (null == nMFeeMap) continue;

            // 该人 该项目 最新的一条缴费记录
            List<PaymentBean> paymentBeanList = nMFeeMap.get(key);

            // 该人 该项目 没交过钱
            if (CollectionUtil.isEmpty(paymentBeanList)) continue;

            PaymentBean paymentBean = paymentBeanList.get(0);

            // 该人 该项目 最新的一条缴费记录里的结束时间
            String endTime;
            if (paymentBean.getPaymentStatus().equals("0")) {
                // 判断 如果缴费则取结束时间
                endTime = paymentBean.getEndtime();
            } else {
                // 退费则取开始时间
                endTime = paymentBean.getBegtime();
            }

            // 若 该人 该项目 最新的一条缴费记录里的结束时间 为空 不处理【数据有问题】
            if (StringUtils.isEmpty(endTime)) continue;
            Date end = DateUtils.parseDate(endTime);

            // 启动项目 的开始时间
            String beg = paPayserviceBean.getBegDate();

            // 如果 启动项目的开始时间为空 不处理 【数据有问题】
            if (StringUtils.isEmpty(beg)) continue;
            Date begDate = DateUtils.parseDate(beg);

            String endDate = paPayserviceBean.getEndDate();
            Date ends = null;
            if (!StringUtils.isEmpty(endDate)) {
                ends = DateUtils.parseDate(endDate);
            }

            long time = begDate.getTime();
            long time1 = end.getTime();
            // 该人 该项目 启动项目的开始时间 > 该人 该项目 最新的一条缴费记录里的结束时间【即：这条启动项目完全欠费】
            if (time > time1) continue;

            if (null == ends || ends.compareTo(end) > 0) {
                Date dateAdd = TimeUtil.dateAdd(end, TimeUtil.UNIT_DAY, 1);
                String formatDate = DateUtils.formatDate(dateAdd, "yyyy-MM-dd HH:mm:ss");
                PaPayserviceBean add = new PaPayserviceBean();
                Date now = new Date();
                BeanUtils.copyProperties(paPayserviceBean, add);
                // 该人 该项目 当启动项目开始时间 =< 该人该项目的缴费结束时间时
                // 需将缴费结束时间作为该条启动项目的结束时间 并且将该时间加一天作为新开启的项目的开始时间
                add.setPpId(null);
                add.setBegDate(formatDate);
                add.setCreateDate(now);
                add.setUpdateDate(now);
                add.setChargeFlag(3);
                add.setCreator(88888);
                add.setReviser(88888);
                addList.add(add);

                paPayserviceBean.setEndDate(endTime);
            }
            paPayserviceBean.setChargeFlag(1);
            patchList.add(paPayserviceBean);
        }

        if (!CollectionUtil.isEmpty(patchList)) {
            for (PaPayserviceBean paPayserviceBean : patchList) {
                toHisDao.patchPPById(paPayserviceBean);
            }
        }
        if (!CollectionUtil.isEmpty(addList)) {
            for (PaPayserviceBean paPayserviceBean : addList) {
                toHisDao.addPP(paPayserviceBean);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importSection(Date date) {

        // 该人 该项目 最新的一条缴费记录
        List<PaymentBean> newestByPAndS = toHisDao.getNewestByPAndS();

        Map<String, List<PaymentBean>> payMap = null;
        if (!CollectionUtil.isEmpty(newestByPAndS)) {
            payMap = newestByPAndS.stream().collect(Collectors.groupingBy(item -> item.getPatientId() + "|" + item.getPayserviceId()));
        }

        List<PaPayserviceBean> sectionList = toHisDao.getSection();
        List<PaPayserviceBean> patchList = new ArrayList<>();
        List<PaPayserviceBean> addList = new ArrayList<>();
        for (PaPayserviceBean paPayserviceBean : sectionList) {
            String key = paPayserviceBean.getPatientId() + "|" + paPayserviceBean.getPayserviceId();
            String begDate = paPayserviceBean.getBegDate();
            Date beg = DateUtils.parseDate(begDate);

            String endDate = paPayserviceBean.getEndDate();
            if (StringUtils.isEmpty(endDate)) {
                // 没有结束时间 数据有问题
                continue;
            }
            Date end = DateUtils.parseDate(endDate);


            // 所有人都没交过钱 【欠费】
            if (null == payMap) continue;

            List<PaymentBean> paymentBeans = payMap.get(key);

            // 该人 该项目 没交过钱
            if (CollectionUtil.isEmpty(paymentBeans)) continue;

            PaymentBean paymentBean = paymentBeans.get(0);

            String endTime;
            if (paymentBean.getPaymentStatus().equals("0")) {
                // 判断 如果缴费则取结束时间
                endTime = paymentBean.getEndtime();
            } else {
                // 退费则取开始时间
                endTime = paymentBean.getBegtime();
            }

            // 没有结束时间 数据有问题
            if (StringUtils.isEmpty(endTime)) continue;

            Date parseDate = DateUtils.parseDate(endTime);
            Date dateAdd = TimeUtil.dateAdd(parseDate, TimeUtil.UNIT_DAY, 1);

            long time = parseDate.getTime();
            long time1 = beg.getTime();
            if (time1 > time) {
                // 完全欠费 不处理
                continue;
            }

            if (parseDate.compareTo(end) < 0) {
                // 取差值(将缴费结束时间作为启动项开始时间)
                String formatDate = DateUtils.formatDate(dateAdd, "yyyy-MM-dd HH:mm:ss");
                PaPayserviceBean tar = new PaPayserviceBean();
                BeanUtils.copyProperties(paPayserviceBean, tar);
                tar.setBegDate(formatDate);
                tar.setPpId(null);
                tar.setIsUse("3");
                tar.setReviser(88888);
                tar.setChargeFlag(3);
                tar.setCreateDate(date);
                tar.setCreator(88888);
                addList.add(tar);

                paPayserviceBean.setEndDate(endTime);
            }
            paPayserviceBean.setChargeFlag(1);
            patchList.add(paPayserviceBean);
        }

        if (!CollectionUtil.isEmpty(patchList)) {
            for (PaPayserviceBean paPayserviceBean : patchList) {
                toHisDao.patchPP(paPayserviceBean);
            }
        }

        if (!CollectionUtil.isEmpty(addList)) {
            for (PaPayserviceBean paPayserviceBean : addList) {
                toHisDao.addPP(paPayserviceBean);
            }
        }
    }

    // 请求his获取交易数据
    private Map<String, JSONObject> getHisData(String start, String end) {
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        object.put("billStartDate", start);
        object.put("billEndDate", end);
        Object[] objects = toHisTask.requestHis("Z050", object.toJSONString());
        Map<String, JSONObject> hisDataMap = new HashMap<>();
        if (null == objects || objects.length <= 0) return new HashMap<>();
        for (Object o : objects) {

            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (!jsonObject.containsKey("item")) continue;

            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;

            for (Object obj : jsonArray) {
                JSONObject entity = JSONObject.parseObject(obj.toString());
                Object kh = entity.get("Kh");
                Object tradeno = entity.get("TradeNo");
                String key = kh + "|" + tradeno;
                hisDataMap.put(key, entity);
            }
        }

        return hisDataMap;
    }

}
