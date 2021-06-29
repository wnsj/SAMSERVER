package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.*;
import com.jiubo.sam.dto.ConfirmClosedDto;
import com.jiubo.sam.dto.PatientMoneyCount;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.*;
import com.jiubo.sam.util.CollectionsUtils;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.swing.text.DateFormatter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 患者基础信息 服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientDao, PatientBean> implements PatientService {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaPayserviceDao paPayserviceDao;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PatienttypeService patienttypeService;

    @Autowired
    private MedicinsurtypeService medicinsurtypeService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private PaPayserviceService paPayserviceService;

    @Autowired
    private MedicalExpensesService medicalExpensesService;

    @Autowired
    private MedicalExpensesDao medicalExpensesDao;

    @Autowired
    private PatinetMarginDao patinetMarginDao;


    @Autowired
    private AdmissionRecordsService admissionRecordsService;

    @Override
    public PatientBean queryPatientByHospNum(PatientBean patientBean) throws MessageException {
        QueryWrapper<PatientBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "HOSP_NUM", patientBean.getHospNum());
        PatientBean bean = patientDao.selectOne(queryWrapper);
        List<PaymentBean> paymentBeans = new ArrayList<PaymentBean>();
        if (bean != null) {
            //查询所有的收费项目
            paymentBeans = paymentService.queryPaymentByPatientId(bean.getPatientId());
            //查询现有的收费项目

            bean.setPaymentList(paymentBeans);
        }
        return bean;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<PatientBean> queryPatient(String page, String pageSize, PatientBean patientBean) throws Exception {
        if (StringUtils.isBlank(page)) {
            page = "1";
        }
        if (StringUtils.isBlank(pageSize)) {
            pageSize = "10";
        }
        Page<PatientBean> result = new Page<>(Long.valueOf(page), Long.valueOf(pageSize));

        List<PatientBean> pbList = patientDao.queryPatient(result, patientBean);

       /* if (patientDao.queryPatient(result, patientBean).size()>0){
            for (int i=0;i < pbList.size();i++) {
                PatientBean pb = pbList.get(i);
                Map<String,Object> tatol = this.patientArrears(pb);
                pbList.get(i).setMedicalTatol(String.valueOf(tatol.get("medicalTatol")));
                pbList.get(i).setPaymentArrears(String.valueOf (((Map<String, Object>) tatol.get("paymentArrears")).get("TOTAL")));
            }
        }*/
        if (!CollectionUtils.isEmpty(pbList)) {
//            List<MedicalExpensesBean> hospNums;
            List<PayTotalDto> payTotalList;
            if (pageSize.equals("-1")) {
                List<String> strings = pbList.stream().map(PatientBean::getHospNum).collect(Collectors.toList());
//                hospNums = medicalExpensesDao.getMeByHospNumsPage(strings);
                payTotalList = paymentDao.getPayTotalPage(strings);
            } else {
//                hospNums = medicalExpensesDao.getMeByHospNums(patientBean);
                payTotalList = paymentDao.getPayTotal(patientBean);
            }

//            Map<String, List<MedicalExpensesBean>> meMap = null;
//            if (!CollectionUtils.isEmpty(hospNums)) {
//                meMap = hospNums.stream().collect(Collectors.groupingBy(MedicalExpensesBean::getHospNum));
//            }


            Map<String, List<PayTotalDto>> payMap = null;
            if (!CollectionUtils.isEmpty(payTotalList)) {
                payMap = payTotalList.stream().collect(Collectors.groupingBy(PayTotalDto::getHospNum));
            }

            for (PatientBean patient : pbList) {
//                patient.setMedicalTatol("0");
                patient.setPaymentArrears("0");
//                if (null != meMap) {
//                    List<MedicalExpensesBean> beanList = meMap.get(patient.getHospNum());
//                    if (!CollectionUtils.isEmpty(beanList)) {
//                        BigDecimal decimal = beanList.stream()
//                                .map(item -> new BigDecimal(StringUtils.isBlank(item.getDepositFee()) ? "0" : item.getDepositFee())
//                                .add(new BigDecimal(StringUtils.isBlank(item.getArrearsFee()) ? "0" : item.getArrearsFee()))
//                                        .add(new BigDecimal(StringUtils.isBlank(item.getRealFee()) ? "0" : item.getRealFee())))
//                                .reduce(BigDecimal.ZERO, BigDecimal::add);
//                        if (BigDecimal.ZERO.compareTo(decimal) > 0) {
//                            decimal = decimal.multiply(BigDecimal.valueOf(-1));
//                        }
//                        patient.setMedicalTatol(String.valueOf(decimal));
//                    }
//                }
                if (null != payMap) {
                    List<PayTotalDto> dtoList = payMap.get(patient.getHospNum());
                    if (!CollectionUtils.isEmpty(dtoList)) {
                        patient.setPaymentArrears(String.valueOf(dtoList.get(0).getTotal()));
                    }
                }
            }
        }
        for (PatientBean bean : pbList) {
            Double money = bean.getMoney();
            money = money * -1;
            bean.setMoney(money);
        }
        Page<PatientBean> patientBeanPage = result.setRecords(pbList);
        return patientBeanPage;
    }

    public PatientBean accurateQuery(PatientBean patientBean) {
        PatientBean bean = new PatientBean();
        List<PatientBean> pbList = new ArrayList<PatientBean>();
        List<PaymentBean> paymentBeans = new ArrayList<PaymentBean>();
        System.out.println("传来的住院号" + patientBean.getHospNum());
        pbList = patientDao.accurateQuery(patientBean);
        if (pbList.size() > 0) {
            bean = pbList.get(0);
        } else {
            bean = null;
        }
        if (bean != null) {
            //查询所有的收费项目
            paymentBeans = paymentService.queryPaymentByPatientId(bean.getPatientId());
            bean.setPaymentList(paymentBeans);
        }
        return bean;
    }

    public PatientBean fuzzyQuery(PatientBean patientBean) throws Exception {
        PatientBean bean = new PatientBean();
        List<PatientBean> pbList = new ArrayList<PatientBean>();
        List<PaymentBean> paymentBeans = new ArrayList<PaymentBean>();
        pbList = patientDao.fuzzyQuery(patientBean);
        if (pbList.size() > 0) {
            bean = pbList.get(0);
            //查询所有的收费项目
            //paymentBeans = paymentService.queryPaymentByPatientId(bean.getPatientId());
            paymentBeans = paymentService.queryPaymentByHospNum(bean.getHospNum(), bean.getPatientId());
            bean.setPaymentList(paymentBeans);
        }
        return bean;
    }

    @Override
    public PatientBean queryPatientPaymentByIdTime(Map<String, Object> map) throws MessageException {
        if (map == null || map.get("patientId") == null || StringUtils.isBlank(String.valueOf(map.get("patientId"))) || map.get("paymentTime") == null || StringUtils.isBlank(String.valueOf(map.get("paymentTime"))))
            throw new MessageException("患者Id或交费时间为空!");
        PatientBean patientBean = new PatientBean();
        patientBean.setPatientId(String.valueOf(map.get("patientId")));
        List<PatientBean> patientBeans = patientDao.queryPatient(patientBean);
        if (patientBeans.size() <= 0) throw new MessageException("请检查患者Id是否正确!");
        PatientBean bean = patientBeans.get(0);
        List<PaymentBean> paymentBeans = paymentService.queryPaymentByPatientIdTime(map);
        if (!CollectionsUtils.isEmpty(paymentBeans)) {
            DepartmentBean departmentBean = departmentDao.selectById(paymentBeans.get(0).getDeptId());
            bean.setDeptId(paymentBeans.get(0).getDeptId());
            bean.setDeptName(departmentBean.getName());
        }
        bean.setPaymentList(paymentBeans);
        return bean;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatientBean addPatient(PatientBean patientBean) throws Exception {
        String idCard = patientBean.getIdCard();
        String hospNum1 = patientBean.getHospNum();
        QueryWrapper<PatientBean> patientBeanQueryWrapper = new QueryWrapper<>();
        List<PatientBean> patientBeans1 = patientDao.selectList(patientBeanQueryWrapper);
        for (PatientBean bean : patientBeans1) {
            if (bean.getHospNum().equals(hospNum1)) {
                continue;
            }
            if (bean.getIdCard().equals(idCard)) throw new MessageException("身份证号不能重复");
        }


        //查询患者信息
        PatientBean patient = queryPatientByHospNum(patientBean);

        String nowStr = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime());
        Date date = new Date();
        patientBean.setUpdateTime(date);
        patientBean.setCreateDate(date);
        //如果患者出院，停止所有收费项目
        if ("0".equals(patientBean.getInHosp())) {
           /* String outHosp = patientBean.getOutHosp();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(outHosp,df);
            LocalDate localDate = ldt.toLocalDate();
            LocalDate now = LocalDate.now();
            if (localDate.isAfter(now)||localDate.equals(now)) {

        QueryWrapper<PaPayserviceBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "PP_ID", paPayserviceBean.getPpId());
        List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectList(queryWrapper);
        if (paPayserviceBeans.size() <= 0) {
            throw new MessageException("没有开启无法修改");
        } else {
            //添加日志
            paPayserviceDao.updatePaPayService(paPayserviceBean);
            if (paPayserviceBean.getHospNum() != "" && paPayserviceBean.getHospNum() != null) {
                logRecordsService.insertLogRecords(new LogRecordsBean()
                        .setHospNum(paPayserviceBean.getHospNum())
                        .setOperateId(Integer.valueOf(paPayserviceBean.getAccount()))
                        .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                        .setOperateModule("启动项目管理")
                        .setOperateType("修改")
                        .setLrComment(paPayserviceBean.toString())
                );
            }
        }
    }

            */


            String outHosp = patientBean.getOutHosp();
            if (outHosp == null || outHosp == "") {
                throw new MessageException("出院时间必填");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Long beginUseTime = sdf.parse(outHosp).getTime();
            Long endTimeLong = beginUseTime - 86400000;
            SimpleDateFormat sdfg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String sd = sdfg.format(new Date(Long.parseLong(String.valueOf(endTimeLong))));      // 时间戳转换成时间

            /*DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime ldt = LocalDateTime.parse(outHosp,df);
            System.out.println(ldt);*/


            String hospNum = patientBean.getHospNum();
            List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectPaPayService(hospNum);
           /* if (paPayserviceBeans.size() >= 1) {
                //有开启的项目

                for (PaPayserviceBean paPayserviceBean : paPayserviceBeans) {
                    String isUse = paPayserviceBean.getIsUse();
                    if () {
                        paPayserviceBean.setIsUse("0");
                        paPayserviceBean.setEndDate(sd);
                    }
                    paPayserviceDao.updateById(paPayserviceBean);
                }
            }*/


//            LocalDateTime now = LocalDateTime.now();
//            paPayserviceDao.updatePaPayServiceByPatient(patientBean.getHospNum(), now);
            //}
        }

        //添加出入院记录
        admissionRecordsService.addAdmissionRecord(new AdmissionRecordsBean()
                .setHospNum(patientBean.getHospNum())
                .setIsHos(Integer.parseInt(patientBean.getInHosp()))
                .setArInDate(patientBean.getHospTime())
                .setArOutDate(patientBean.getOutHosp())
                .setCreateDate(nowStr));


        if (patient == null) {
//            patientBean.setHospTime(nowStr);
            //插入患者信息
            patientBean.setCreator(patientBean.getCreator());
            patientBean.setReviser(patientBean.getCreator());
            patientDao.addPatient(patientBean);
        } else {
            patientBean.setReviser(patientBean.getCreator());
            List<PatientBean> patientBeans = new ArrayList<>();
            patientBeans.add(patientBean);
            //修改患者信息
            patientDao.saveOrUpdate(patientBeans);
        }


        return patientBean;
//        if (patientBean.getPaymentList() != null && patientBean.getPaymentList().size() > 0) {
//            for (PaymentBean paymentBean : patientBean.getPaymentList()) {
//                paymentBean.setPatientId(patientBean.getPatientId());
//                paymentBean.setUpdatetime(nowStr);
//                //插入交费信息
//                paymentService.addPayment(paymentBean);
//            }
//        }
    }

    @Override
    @Transactional
    public void addPatientList(Map<Object, Object> map, String accountId) throws Exception {
        Map<String, DepartmentBean> deptMap = new HashMap<String, DepartmentBean>();
        Map<String, PatienttypeBean> patientTypeMap = new HashMap<String, PatienttypeBean>();
        Map<String, MedicinsurtypeBean> miTypeMap = new HashMap<String, MedicinsurtypeBean>();

        List<PatientBean> patientBeans = new ArrayList<PatientBean>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + "##" + entry.getValue());
            PatientBean patientBean = new PatientBean();
            patientBean.setAccountId(accountId);
            List list = (List) entry.getValue();
            if (list == null || list.size() <= 0 || entry.getValue() == null) continue;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                switch (i) {
                    case 0:
                        patientBean.setHospNum(String.valueOf(list.get(0)));
                        break;
                    case 1:
                        patientBean.setName(String.valueOf(list.get(1)));
                        break;
                    case 2:
                        if (list.get(2) != null && StringUtils.isNotBlank(String.valueOf(String.valueOf(list.get(2))))
                                && Double.valueOf(String.valueOf(list.get(2))) > 0)
                            patientBean.setAge(String.valueOf(Double.valueOf(String.valueOf(list.get(2))).intValue()));
                        break;
                    case 3:
                        if (list.get(3) == null) {
                            patientBean.setSex("3");
                        } else {
                            String sex = String.valueOf(list.get(3));
                            if ("男".equals(sex)) {
                                sex = "1";
                            } else if ("女".equals(sex)) {
                                sex = "2";
                            } else {
                                sex = "3";
                            }
                            patientBean.setSex(sex);
                        }
                        break;
                    case 4:
                        if (list.get(4) != null) {
                            String dpetName = String.valueOf(list.get(4));
                            if (StringUtils.isNotBlank(dpetName)) {
                                DepartmentBean bean = deptMap.get(dpetName);
                                if (bean != null) {
                                    patientBean.setDeptId(bean.getDeptId());
                                } else {
                                    bean = new DepartmentBean();
                                    bean.setName(dpetName);
                                    List<DepartmentBean> departmentBeans = departmentService.queryDeptByName(bean);
                                    if (departmentBeans != null && departmentBeans.size() > 0) {
                                        patientBean.setDeptId(departmentBeans.get(0).getDeptId());
                                        deptMap.put(dpetName, departmentBeans.get(0));
                                    }
                                }
                            }
                        }
                        break;
                    case 5:
                        if (list.get(5) != null && StringUtils.isNotBlank(String.valueOf(list.get(5)))) {
                            String inHosp = String.valueOf(list.get(5));
                            if ("在".equals(inHosp) || "是".equals(inHosp)) {
                                inHosp = "1";
                            } else {
                                inHosp = "0";
                            }
                            patientBean.setInHosp(inHosp);
                        }
                        break;
                    case 6:
                        if (list.get(6) != null && StringUtils.isNotBlank(String.valueOf(list.get(6)))) {
                            String hospTime = null;
                            if (list.get(6) instanceof Date) {
                                hospTime = TimeUtil.getDateYYYY_MM_DD((Date) list.get(6));
                            } else if (list.get(6) instanceof String) {
                                hospTime = String.valueOf(list.get(6));
                            }
                            patientBean.setHospTime(hospTime);
                        }
                        break;
                    case 7:
                        if (list.get(7) != null && StringUtils.isNotBlank(String.valueOf(list.get(7)))) {
                            String outHosp = null;
                            if (list.get(7) instanceof Date) {
                                outHosp = TimeUtil.getDateYYYY_MM_DD((Date) list.get(7));
                            } else if (list.get(7) instanceof String) {
                                outHosp = String.valueOf(list.get(7));
                            }
                            patientBean.setOutHosp(outHosp);
                        }
                        break;
                    case 8:
                        if (list.get(8) != null && StringUtils.isNotBlank(String.valueOf(list.get(8)))) {
                            String patiTypeName = String.valueOf(list.get(8));
                            PatienttypeBean patienttypeBean = patientTypeMap.get(patiTypeName);
                            if (patienttypeBean != null) {
                                patientBean.setPatitypeid(patienttypeBean.getPatitypeid());
                            } else {
                                patienttypeBean = new PatienttypeBean();
                                patienttypeBean.setPatitypename(patiTypeName);
                                List<PatienttypeBean> patienttypeBeans = patienttypeService.queryPatientTypeByName(patienttypeBean);
                                if (patienttypeBeans != null && patienttypeBeans.size() > 0) {
                                    patienttypeBean = patienttypeBeans.get(0);
                                    patientBean.setPatitypeid(patienttypeBean.getPatitypeid());
                                    patientTypeMap.put(patienttypeBean.getPatitypename(), patienttypeBean);
                                }
                            }
                        }
                        break;
                    case 9:
                        if (list.get(9) != null && StringUtils.isNotBlank(String.valueOf(list.get(9)))) {
                            String miTypeName = String.valueOf(list.get(9));
                            MedicinsurtypeBean medicinsurtypeBean = miTypeMap.get(miTypeName);
                            if (medicinsurtypeBean != null) {
                                patientBean.setMitypeid(medicinsurtypeBean.getMitypeid());
                            } else {
                                medicinsurtypeBean = new MedicinsurtypeBean();
                                medicinsurtypeBean.setMitypename(miTypeName);
                                List<MedicinsurtypeBean> medicinsurtypeBeans = medicinsurtypeService.queryMedicinsurtypeByName(medicinsurtypeBean);
                                if (medicinsurtypeBeans != null && medicinsurtypeBeans.size() > 0) {
                                    medicinsurtypeBean = medicinsurtypeBeans.get(0);
                                    patientBean.setMitypeid(medicinsurtypeBean.getMitypeid());
                                    miTypeMap.put(medicinsurtypeBean.getMitypename(), medicinsurtypeBean);
                                }
                            }
                        }
                        break;
                }
            }
            patientBeans.add(patientBean);
        }
        //分批处理
        List<List<PatientBean>> lists = splitList(patientBeans, 60);
        if (lists == null) throw new MessageException("数据错误!");
        for (List<PatientBean> list : lists) {
            patientDao.saveOrUpdate(list);
        }
    }

    @Override
    public List<PatientBean> queryGatherNewPayment(PatientBean patientBean) throws MessageException, Exception {
        return patientDao.queryGatherNewPayment(patientBean);
    }

    //空调费启动，停止
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startUpPayService(PatientBean patientBean) throws Exception {
        if (patientBean.getIsStart() != 1 && patientBean.getIsStart() != 0) throw new MessageException("请填写启动表示");
        PayserviceBean payserviceBean = selectIsUse(42);
        String price = payserviceBean.getPrice();
        List<PatientBean> patientBeans = patientDao.queryPatient(patientBean);
        if (patientBeans.size() > 0 && patientBean.getIsStart() == 1) {
            for (int i = 0; i < patientBeans.size(); i++) {
                paPayserviceService.addAndUpdatePps(new PaPayserviceBean()
                        .setPayserviceId("42")
                        .setPatientId(patientBeans.get(i).getPatientId())
                        .setAccount(patientBean.getAccountId())
                        .setPayType("0")
                        .setIsUse("1")
                        .setDeptId(Integer.valueOf(patientBeans.get(i).getDeptId()))
                        .setIdCard(patientBeans.get(i).getIdCard())
                        .setHospNum(patientBeans.get(i).getHospNum())
                        .setUnitPrice(price)
                        .setReviser(Integer.valueOf(patientBean.getAccountId()))
                        .setCreator(Integer.valueOf(patientBean.getAccountId()))

                );
            }
        } else if (patientBeans.size() > 0 && patientBean.getIsStart() == 0) {
            for (int i = 0; i < patientBeans.size(); i++) {
                paPayserviceDao.updatePaPayService(new PaPayserviceBean()
                        .setPayserviceId("42")
                        .setPatientId(patientBeans.get(i).getPatientId())
                        .setAccount(patientBean.getAccountId())
                        .setPayType("0")
                        .setIsUse("0")
                        .setDeptId(Integer.valueOf(patientBeans.get(i).getDeptId()))
                        .setIdCard(patientBeans.get(i).getIdCard())
                        .setHospNum(patientBeans.get(i).getHospNum())
                        .setUnitPrice(price)
                        .setReviser(Integer.valueOf(patientBean.getAccountId()))
                );
            }
        }
    }

    @Override
    public Map<String, Object> patientArrears(PatientBean patientBean) throws Exception {
        String hospNum = patientBean.getHospNum();
        if (hospNum == null) {
            throw new MessageException("hospNum必传");
        }
        Map<String, Object> dataMap = new HashMap<>();
//        Double medicalTatol=0.00d;
//        List<MedicalExpensesBean> medicalExpensesBeans= medicalExpensesService.queryMedicalExpenses(new MedicalExpensesBean().setHospNum(patientBean.getHospNum()));
        Map<String, Object> paymentArrears = paymentService.queryGatherPaymentListInfo(new PatientBean().setHospNum(patientBean.getHospNum()).setIsMerge("1"));
        QueryWrapper<PatinetMarginBean> qw = new QueryWrapper<>();
        qw.eq("HOSP_NUM", patientBean.getHospNum());
        PatinetMarginBean patinetMarginBean = patinetMarginDao.selectOne(qw);
//        if (medicalExpensesBeans.size()>0){
//            for (int i=0;i<medicalExpensesBeans.size();i++){
//                String depositFee = medicalExpensesBeans.get(i).getDepositFee();
//                String arrearsFee = medicalExpensesBeans.get(i).getArrearsFee();
//                String realFee = medicalExpensesBeans.get(i).getRealFee();
//                if (StringUtils.isEmpty(depositFee)){
//                    depositFee="0";
//                }
//                if (StringUtils.isEmpty(arrearsFee)){
//                    arrearsFee="0";
//                }
//                if (StringUtils.isEmpty(realFee)){
//                    realFee="0";
//                }
//                medicalTatol = medicalTatol + (Double.valueOf(depositFee) + Double.valueOf(arrearsFee) + Double.valueOf(realFee));
//            }
//        }
//        if (medicalTatol<0){
//            dataMap.put("medicalTatol",new java.text.DecimalFormat("#.000").format(medicalTatol*-1));
//        }else {
//            dataMap.put("medicalTatol",0);
//        }
        if (patinetMarginBean == null) {
            dataMap.put("medicalTatol", 0);
        } else {
            dataMap.put("medicalTatol", -1 * patinetMarginBean.getMoney());
        }
        dataMap.put("paymentArrears", paymentArrears.get("paymentTotal"));
        // 患者管理明细 底部 预交金余额 医疗缴费汇总 非医疗缴费汇总
        PatientMoneyCount pmc = patientDao.getPmc(patientBean.getHospNum());
        dataMap.put("patientMoneyCount", pmc);
        return dataMap;
    }

    /* *
     * @Author wxg
     * @Description 根据住院号hospNum修改维护医生empId
     * @Date 2020/12/9 9:10
     * @Param [patientBean]
     * @Return void
     */
    @Override
    public void updateDoctorByHospNum(PatientBean patientBean) {
        patientDao.updateDoctorByHospNum(patientBean);
    }

    @Override
    @Transactional
    public Boolean confirmClosed(ConfirmClosedDto confirmClosedDto) throws MessageException {
        String hospNum = confirmClosedDto.getHospNum();
        String outHosp = confirmClosedDto.getOutHosp();
        List<PaPayserviceBean> paPayserviceBeanList = paPayserviceDao.selectByHospNumAndOutHosp(hospNum, outHosp);
        if (CollectionUtils.isEmpty(paPayserviceBeanList)) {
            //没有开启的项目，可以关闭
            return true;
        } else {
            //有开启的项目
            Integer i = 0;
            for (PaPayserviceBean paPayserviceBean : paPayserviceBeanList) {
                String begDate = paPayserviceBean.getBegDate();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDateTime begDateLdt = LocalDateTime.parse(begDate, df);
                LocalDate begDateDate = begDateLdt.toLocalDate();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate outHospLdt = LocalDate.parse(outHosp, fmt);
                if (begDateDate.isBefore(outHospLdt)) {
                    //开始时间小于出院时间
                    i++;
                }
            }
            if (paPayserviceBeanList.size() == i) {
                //假如全部开始时间小于出院时间
                for (PaPayserviceBean paPayserviceBean : paPayserviceBeanList) {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate outHospLdt = LocalDate.parse(outHosp, df);
                    LocalDate localDateTime = outHospLdt.plusDays(-1);
                    String localTime = df.format(localDateTime);
                    paPayserviceBean.setEndDate(localTime);
                    paPayserviceBean.setIsUse("0");
                    paPayserviceDao.updateById(paPayserviceBean);
                }
            } else {
                return false;
            }
            return true;
        }
    }

    @Override
    @Transactional
    public void lose(ConfirmClosedDto confirmClosedDto) throws MessageException {

        String hospNum = confirmClosedDto.getHospNum();
        String outHosp = confirmClosedDto.getOutHosp();
        Integer lose = confirmClosedDto.getLose();
        if (lose == 1) {//失效
            List<PaPayserviceBean> paPayserviceBeanList = paPayserviceDao.selectOpenByHospNumAndOutHosp(hospNum, outHosp);
            if (!CollectionUtils.isEmpty(paPayserviceBeanList)) {
                for (PaPayserviceBean paPayserviceBean : paPayserviceBeanList) {
                    paPayserviceBean.setIsUse("2");
                    paPayserviceDao.updateById(paPayserviceBean);
                }
            }
            List<PaPayserviceBean> paPayserviceBeanList1 = paPayserviceDao.selectByHospNumAndOutHosps(hospNum, outHosp);
            if (!CollectionUtils.isEmpty(paPayserviceBeanList1)) {
                for (PaPayserviceBean paPayserviceBean : paPayserviceBeanList1) {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate outHospLdt = LocalDate.parse(outHosp, df);
                    LocalDate localDateTime = outHospLdt.plusDays(-1);
                    String localTime = df.format(localDateTime);
                    paPayserviceBean.setEndDate(localTime);
                    paPayserviceBean.setIsUse("2");
                    paPayserviceDao.updateById(paPayserviceBean);
                }
            }
        } else {//不失效
            throw new MessageException("请手动修改项目结束时间");
        }
    }

    @Override
    public PayserviceBean selectIsUse(Integer i) {
        return paPayserviceDao.selectIsUse(i);

    }

    @Override
    @Transactional
    public List<PatientBean> queryPatientListByHospNum(Map<Object, Object> map, String accountId) throws ParseException, Exception {
        Set<PatientBean> patientSet = new HashSet<>();
        List<PatientBean> patientList = new ArrayList<PatientBean>();
        List<PaymentBean> paymentBeanList = new ArrayList<PaymentBean>();

        for (int j = 2; j < map.size() + 2; j++) {
            PatientBean patientBean = new PatientBean();
            PaymentBean paymentBean = new PaymentBean();
            List<Object> list = (List<Object>) map.get(j);
            if (list == null || list.size() <= 0) continue;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (list.get(0) != null && StringUtils.isNotBlank(String.valueOf(list.get(0)))) {
                    patientBean.setHospNum(String.valueOf(list.get(0)));
                } else {
                    continue;
                }

                int payserviceId;

                /* 查询住院号是否存在 */
                QueryWrapper<PatientBean> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("*");
                queryWrapper.eq(true, "HOSP_NUM", patientBean.getHospNum());
                PatientBean bean = patientDao.selectOne(queryWrapper);

                if (bean == null) {
                    if (patientSet.add(patientBean)) {
                        patientList.add(patientBean);
                    }
                    break;
                } else {
                    paymentBean.setPatientId(bean.getPatientId());
                    paymentBean.setAccountId(accountId);
                    paymentBean.setIsuse(true);
                    switch (i) {
                        case 2:
                            if (list.get(2) != null && StringUtils.isNotBlank(String.valueOf(list.get(2)))) {
                                String paymenttime = null;
                                if (list.get(2) instanceof Date) {
                                    paymenttime = TimeUtil.getDateYYYY_MM_DD((Date) list.get(2));
                                } else if (list.get(2) instanceof String) {
                                    paymenttime = String.valueOf(list.get(2));
                                }
                                paymentBean.setPaymentTime(paymenttime);
                            }

                            break;
                        case 3:
                            if (list.get(3) != null && StringUtils.isNotBlank(String.valueOf(list.get(3)))) {
                                payserviceId = Double.valueOf(String.valueOf(list.get(3))).intValue();
                                paymentBean.setPayserviceId(String.valueOf(payserviceId));
                            }
                            break;
                        case 5:
                            if (list.get(5) != null && StringUtils.isNotBlank(String.valueOf(list.get(5))))
                                paymentBean.setPrice(Double.valueOf(String.valueOf(list.get(5))));
                            break;
                        case 6:
                            if (list.get(6) != null && StringUtils.isNotBlank(String.valueOf(list.get(6))))
                                paymentBean.setReceivable(Double.valueOf(String.valueOf(list.get(6))));
                            break;
                        case 7:
                            if (list.get(7) != null && StringUtils.isNotBlank(String.valueOf(list.get(7))))
                                paymentBean.setActualpayment(Double.valueOf(String.valueOf(list.get(7))));

                            break;
                        case 8:
                            if (list.get(8) != null && StringUtils.isNotBlank(String.valueOf(list.get(8)))) {
                                String brginTime = null;
                                if (list.get(8) instanceof Date) {
                                    brginTime = TimeUtil.getDateYYYY_MM_DD((Date) list.get(8));
                                } else if (list.get(8) instanceof String) {
                                    brginTime = String.valueOf(list.get(8));
                                }
                                paymentBean.setBegtime(brginTime);
                            }
                            break;
                        case 9:
                            if (list.get(9) != null && StringUtils.isNotBlank(String.valueOf(list.get(9)))) {
                                String endTime = null;
                                if (list.get(9) instanceof Date) {
                                    endTime = TimeUtil.getDateYYYY_MM_DD((Date) list.get(9));
                                } else if (list.get(9) instanceof String) {
                                    endTime = String.valueOf(list.get(9));
                                }
                                paymentBean.setEndtime(endTime);
                            }
                            break;
                    }
                }
            }
            paymentBeanList.add(paymentBean);
        }

        if (patientList == null || patientList.size() <= 0) {
            //分批处理
            List<List<PaymentBean>> lists = splitList(paymentBeanList, 100);
            for (List<PaymentBean> pBeanList : lists) {
                List<PaymentBean> pBList = new ArrayList<>();
                /* 查询缴费信息是否存在 */
                for (int i = 0; i < pBeanList.size(); i++) {
                    PaymentBean pb = pBeanList.get(i);
                    QueryWrapper<PaymentBean> queryWrapper = new QueryWrapper<>();
                    queryWrapper.select("*");
                    queryWrapper.eq(true, "PATIENT_ID", pb.getPatientId());
                    queryWrapper.eq(true, "PAYSERVICE_ID", pb.getPayserviceId());
                    queryWrapper.eq(true, "PAYMENTTIME", pb.getPaymentTime());
                    PaymentBean bean = paymentDao.selectOne(queryWrapper);
                    if (bean == null) {
                        pBList.add(pb);
                    }
                }
                if (pBList.size() > 0)
                    paymentService.addPayment(pBList);

            }
        }
        return patientList;
    }

    //分批处理
    public static <T> List<List<T>> splitList(List<T> list, int items) throws Exception {
        List<List<T>> lists = new ArrayList<List<T>>();
        if (list == null) throw new MessageException("数据为空!");
        //限制条数
        int pointsDataLimit = items > 0 ? items : 1000;
        int size = list.size();

        //判断是否有必要分批
        if (pointsDataLimit > size) {
            //不需要分批
            lists.add(list);
        } else {
            //分批数
            int part = (size + items - 1) / items;
            for (int i = 0; i < part; i++) {
                lists.add(list.subList(i * items, ((i + 1) * items > size ? size : items * (i + 1))));
            }
        }
        return lists;
    }
}


















