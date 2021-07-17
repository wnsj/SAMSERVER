package com.jiubo.sam.schedule;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.*;
import com.jiubo.sam.dto.EmpDepartmentRefDto;
import com.jiubo.sam.dto.FromHisPatient;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.TimeUtil;
import com.jiubo.sam.util.WebApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ToHisTask {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private HospitalPatientService hospitalPatientService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private ToHisDao toHisDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private PatinetMarginDao patinetMarginDao;

    @Autowired
    private EmpDepartmentRefDao empDepartmentRefDao;

//    private static final String url = "http://yfzx.bsesoft.com:8002/sjservice.asmx?wsdl";
//    private static final String url = "http://192.168.10.2:8081/WebService_Sam_Hospital.asmx?wsdl";

//    @Scheduled(cron = "0 0 21 * * ? ")
    @Transactional(rollbackFor = Exception.class)
    public void syncPatientAndAddHP() throws Exception {
        Object[] result = requestHis("Z000", "{\"BalanceMoney\": 500}");
//        String s = WebApiUtil.ReaderFileToString("D:\\/shuju.txt");
//        JSONArray objects = JSONObject.parseArray(s);
        if (result == null) return;
        List<PatientBean> allIdCard = patientDao.getAllIdCard();
        Map<String, List<PatientBean>> paMap = null;
        if (!CollectionUtils.isEmpty(allIdCard)) {
            paMap = allIdCard.stream().collect(Collectors.groupingBy(PatientBean::getIdCard));
        }

//        List<PatinetMarginBean> mByIdCard = patinetMarginDao.getMByIdCard(null);
//        Map<String, List<PatinetMarginBean>> marginMap = null;
//        if (!CollectionUtil.isEmpty(mByIdCard)) {
//            marginMap = mByIdCard.stream().collect(Collectors.groupingBy(PatinetMarginBean::getIdCard));
//        }

        // 住院余额不足500的集合
        List<HospitalPatientBean> toAddHospitalMoney = new ArrayList<>();
        // 患者信息集合
        List<FromHisPatient> fromHisPatientList = new ArrayList<>();
        List<FromHisPatient> fromAddHisPatientList = new ArrayList<>();
        for (Object o : result) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (!jsonObject.containsKey("item")) continue;
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;
            for (Object object : jsonArray) {

                JSONObject entity = JSONObject.parseObject(object.toString());
                // his流水
                String visitSn = entity.getString("VisitSn");
                // 患者姓名
                String patientName = entity.getString("PatientName");
                String sex = entity.getString("Sex");
                String age = entity.getString("Age");
                String idCardNo = entity.getString("IDCardNo");
                // 患者联系方式
                String phoneNo = entity.getString("PhoneNo");
                // 病区编码
                String inPatientAreaNo = entity.getString("InPatientAreaNo");
                // 病区名称
                String inPatientArea = entity.getString("InPatientArea");
                // 科室编码
                String departmentNo = entity.getString("DepartmentNo");
                // 科室名称
                String department = entity.getString("Department");
                // 床号
                String bedNo = entity.getString("BedNo");
                // 登记日期
                String registerDate = entity.getString("RegisterDate");
                // 住院日期
                String admissionDate = entity.getString("AdmissionDate");
                // 出院日期
                String dischargeDate = entity.getString("DischargeDate");
                // 预交押金
                String totalMoney = entity.getString("TotalMoney");
                // 押金余额
                String balanceMoney = entity.getString("BalanceMoney");

                int k = 0;
                if (sex.equals("男")) {
                    k = 1;
                } else if (sex.equals("女")) {
                    k = 2;
                }

                FromHisPatient fromHisPatient = new FromHisPatient();
                fromHisPatient.setAge(age);
                Integer deptId = null;
                if (StringUtils.isNotBlank(departmentNo)) {
                    DepartmentBean departmentBean = toHisDao.getDeByCode(departmentNo);
                    if (null != departmentBean) {
                        deptId = Integer.parseInt(departmentBean.getDeptId());
                        fromHisPatient.setDeptId(deptId);
                    }
                }

                fromHisPatient.setHospBalance(new BigDecimal(balanceMoney));
//                fromHisPatient.setHospNum(visitSn);
                fromHisPatient.setHisWaterNum(visitSn);
                // 不更新入院时间
//                Date hospTime = DateUtils.parseDate(admissionDate);
//                fromHisPatient.setHospTime(hospTime);
                if (!StringUtils.isEmpty(dischargeDate)) {
                    Date outHosp = TimeUtil.parseDateYYYY_MM_DD_HH_MM_SS(dischargeDate);
                    fromHisPatient.setOutHosp(outHosp);
                }
                fromHisPatient.setPatientName(patientName);
                fromHisPatient.setPatientPhone(phoneNo);
                fromHisPatient.setIdCard(idCardNo);
                fromHisPatient.setSex(k);


                String hospNum = null;
                int isNoFunding = 2;
                int isHosp = 1;
                if (null != paMap) {
                    List<PatientBean> patientBeans = paMap.get(idCardNo);
                    if (!CollectionUtils.isEmpty(patientBeans)) {
                        PatientBean patientBean = patientBeans.get(0);
                        hospNum = patientBean.getHospNum();
                        isNoFunding = patientBean.getIsNoFunding();
                        if (patientBean.getInHosp().equals("0")) {
                            isHosp = 2;
                        }
                        fromHisPatientList.add(fromHisPatient);
                    } else {
                        fromHisPatient.setHospNum(inPatientAreaNo);
                        hospNum = inPatientAreaNo;
                        fromAddHisPatientList.add(fromHisPatient);
                    }
                } else {
                    fromHisPatient.setHospNum(inPatientAreaNo);
                    hospNum = inPatientAreaNo;
                    fromAddHisPatientList.add(fromHisPatient);
                }

//                PatinetMarginBean marginBean = null;
//                if (null != marginMap) {
//                    List<PatinetMarginBean> marginBeans = marginMap.get(idCardNo);
//                    marginBean = marginBeans.get(0);
//                }


                if (new BigDecimal("500").compareTo(new BigDecimal(balanceMoney)) >= 0 && isNoFunding == 2) {
                    HospitalPatientBean hospitalPatientBean = new HospitalPatientBean();
                    Date date = new Date();
                    LocalDateTime dateTime = LocalDateTime.now();
                    hospitalPatientBean.setHospNum(hospNum);
                    hospitalPatientBean.setHisWaterNum(visitSn);
                    hospitalPatientBean.setIdCard(idCardNo);
                    hospitalPatientBean.setAccountId(99999);
                    hospitalPatientBean.setPayDate(date);
                    hospitalPatientBean.setIsInHospital(isHosp);
                    hospitalPatientBean.setCreateDate(dateTime);
                    hospitalPatientBean.setUpdateDate(date);
                    hospitalPatientBean.setDeptId(deptId);
                    hospitalPatientBean.setRealCross(new BigDecimal("3000").doubleValue());
                    // 住院
                    hospitalPatientBean.setType(1);
                    // 缴费
                    hospitalPatientBean.setConsumType(1);
                    hospitalPatientBean.setPayDate(new Date());
                    hospitalPatientBean.setCreateDate(LocalDateTime.now());
                    toAddHospitalMoney.add(hospitalPatientBean);
                }
            }
        }
        // 更新患者信息
        if (!CollectionUtils.isEmpty(fromHisPatientList)) {
            for (FromHisPatient fromHisPatient : fromHisPatientList) {
                patientDao.patchPa(fromHisPatient);
            }
        }

        // 插入患者信息
        if (!CollectionUtils.isEmpty(fromAddHisPatientList)) {
            for (FromHisPatient fromHisPatient : fromHisPatientList) {
                patientDao.addPa(fromHisPatient);
            }
        }

        // 住院费缴费
        if (CollectionUtils.isEmpty(toAddHospitalMoney)) return;
        List<String> failedIdCardList = new ArrayList<>();
        List<String> failedWaterNumList = new ArrayList<>();
        for (HospitalPatientBean hospitalPatientBean : toAddHospitalMoney) {
            // 维护缴费记录
            hospitalPatientBean.setAccountId(99999);
            String serialNumber = hospitalPatientService.addHospitalPatient(hospitalPatientBean);
            // 充值押金
            toHisAddHP(hospitalPatientBean, serialNumber,failedIdCardList,failedWaterNumList);
        }

        if (!CollectionUtil.isEmpty(failedIdCardList)) {
            // 回退押金
            for (String idCard : failedIdCardList) {
                patinetMarginDao.rollbackMargin(idCard);
            }
        }

        if (!CollectionUtil.isEmpty(failedWaterNumList)) {
            for (String waterNum : failedWaterNumList) {
                // 将住院费删除
                patinetMarginDao.deleteHpByWaterNum(waterNum);
                // 将明细删除
                patinetMarginDao.deletePdByWaterNum(waterNum);
            }
        }
    }


    private void toHisAddHP(HospitalPatientBean hospitalPatientBean, String serialNumber, List<String> failedList,List<String> failedWaterNumList) throws MessageException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("VisitSn", hospitalPatientBean.getHisWaterNum());
        Date date = new Date();
        String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        jsonObject.put("TranDate", formatDate);
        jsonObject.put("Cusid", hospitalPatientBean.getIdCard());
        jsonObject.put("TradeNo", serialNumber);
        jsonObject.put("PayType", "9943");
        jsonObject.put("Amt", "3000");

        Object[] z003s = requestHis("Z003", jsonObject.toJSONString());
        for (Object o : z003s) {
            JSONObject object = JSONObject.parseObject(String.valueOf(o));
            JSONObject message = object.getJSONObject("message");
            String code = message.getString("code");
            if (!code.equals("1")) {
                failedList.add(hospitalPatientBean.getIdCard());
                failedWaterNumList.add(serialNumber);
            }
        }
    }

    @Scheduled(cron = "0 0 19 * * ? ")
    public void syncDept() {
        Object[] result = requestHis("Z032", "{}");
        if (result == null) return;
        List<DepartmentBean> allDeptCode = departmentDao.getAllDeptCode();
        List<String> list = null;
        if (!CollectionUtils.isEmpty(allDeptCode)) {
            list = allDeptCode.stream().map(DepartmentBean::getDeptCode).collect(Collectors.toList());
        }
        List<DepartmentBean> departmentBeanList = new ArrayList<>();
        List<DepartmentBean> addList = new ArrayList<>();
        for (Object o : result) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (!jsonObject.containsKey("item")) continue;
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;
            for (Object object : jsonArray) {
                DepartmentBean departmentBean = new DepartmentBean();
                JSONObject entity = JSONObject.parseObject(object.toString());
                String deptCode = entity.getString("DeptCode");
                String deptName = entity.getString("DeptName");
                Integer isEnabled = entity.getInteger("IsEnabled");
                departmentBean.setName(deptName);
                departmentBean.setDeptCode(deptCode);
                departmentBean.setIsuse(String.valueOf(isEnabled));
                if (null != list) {
                    if (list.contains(deptCode)) {
                        departmentBeanList.add(departmentBean);
                    } else {
                        addList.add(departmentBean);
                    }
                }

            }
        }
        if (!CollectionUtils.isEmpty(departmentBeanList)) {
            departmentDao.updateDeptBatch(departmentBeanList);
        }

        if (!CollectionUtils.isEmpty(addList)) {
            departmentDao.addBatch(addList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 10 19 * * ? ")
    public void syncEmployee() throws IOException {
        Object[] result = requestHis("Z042", "{}");
//        String result = WebApiUtil.ReaderFileToString("D:\\2021-07-01.txt");
        if (result == null) return;
        List<EmployeeBean> allPerCode = employeeDao.getAllPerCode();
        Map<String, List<EmployeeBean>> empMap = null;
        if (!CollectionUtils.isEmpty(allPerCode)) {
            empMap = allPerCode.stream().collect(Collectors.groupingBy(EmployeeBean::getPerCode));
        }
        List<DepartmentBean> allDeptCode = departmentDao.getAllDeptCode();
        Map<String, List<DepartmentBean>> map = null;
        if (!CollectionUtils.isEmpty(allDeptCode)) {
            map = allDeptCode.stream().collect(Collectors.groupingBy(DepartmentBean::getDeptCode));
        }
        List<String> list = null;
        if (!CollectionUtils.isEmpty(allPerCode)) {
            list = allPerCode.stream().map(EmployeeBean::getPerCode).collect(Collectors.toList());
        }
        List<EmpDepartmentRefDto> refBeanList = new ArrayList<>();
        List<EmployeeBean> employeeBeanList = new ArrayList<>();
//        List<EmployeeBean> addList = new ArrayList<>();
        List<Long> empIdList = new ArrayList<>();
//        JSONArray objects = JSONArray.parseArray(result);
        for (Object o : result) {
            JSONObject object = JSONObject.parseObject(o.toString());
            if (!object.containsKey("item")) continue;
            JSONArray jsonArray = object.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;
            for (Object dto : jsonArray) {
                EmployeeBean employeeBean = new EmployeeBean();
                JSONObject entity = JSONObject.parseObject(dto.toString());
                String doctorCode = entity.getString("DoctorCode");
                String doctorName = entity.getString("DoctorName");
                String deptCode = entity.getString("DeptCode");
                Integer isEnabled = entity.getInteger("IsEnabled");
                employeeBean.setPerCode(doctorCode);
                employeeBean.setEmpName(doctorName);
                if (isEnabled == 1) {
                    employeeBean.setFlag(1L);
                } else {
                    employeeBean.setFlag(2L);
                }

                Integer empId = null;
                if (null != list) {
                    if (list.contains(doctorCode)) {
                        employeeBeanList.add(employeeBean);
                    } else {
//                        addList.add(employeeBean);
                        employeeDao.addEmpHis(employeeBean);
                        empId = Integer.parseInt(String.valueOf(employeeBean.getId()));
                    }
                } else {
//                    addList.add(employeeBean);
                    employeeDao.addEmpHis(employeeBean);
                    empId = Integer.parseInt(String.valueOf(employeeBean.getId()));
                }



                // 医生 科室 关联
                if (null != deptCode) {
                    EmpDepartmentRefDto empDepartmentRefBean = new EmpDepartmentRefDto();
                    String deptId = null;
                    if (null != map) {
                        List<DepartmentBean> departmentBeans = map.get(deptCode);
                        if (!CollectionUtils.isEmpty(departmentBeans)) {
                            deptId = departmentBeans.get(0).getDeptId();
                            empDepartmentRefBean.setDeptId(deptId);
                        }
                    }
                    if (null != empMap) {
                        List<EmployeeBean> employeeBeans = empMap.get(doctorCode);
                        if (!CollectionUtils.isEmpty(employeeBeans)) {
                            empId = Integer.parseInt(String.valueOf(employeeBeans.get(0).getId()));
                        }
                    }
                    empDepartmentRefBean.setEmpId(String.valueOf(empId));
                    empDepartmentRefBean.setCreateDate(new Date());
                    if (StringUtils.isNotBlank(deptId) && null != empId) {
                        refBeanList.add(empDepartmentRefBean);
                        empIdList.add(Long.parseLong(String.valueOf(empId)));
                    }
                }

            }
        }

        // 先删 关联
        if (!CollectionUtils.isEmpty(empIdList)) {
            employeeDao.deleteAllRef(empIdList);
        }

        if (!CollectionUtils.isEmpty(employeeBeanList)) {
            for (EmployeeBean employeeBean : employeeBeanList) {
                employeeDao.patchEmp(employeeBean);
            }
        }



        // 建立关联
        if (!CollectionUtils.isEmpty(refBeanList)) {
            empDepartmentRefDao.addEdRefDto(refBeanList);
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

}

