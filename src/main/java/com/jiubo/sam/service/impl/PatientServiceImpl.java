package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.DepartmentDao;
import com.jiubo.sam.dao.PaPayserviceDao;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.dao.PaymentDao;
import com.jiubo.sam.dao.PayserviceDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.util.CollectionsUtils;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.text.ParseException;
import java.util.*;

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
    private PaPayserviceService payserviceService;

    @Autowired
    private MedicinsurtypeService medicinsurtypeService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private PaPayserviceService paPayserviceService;

    @Autowired
    private  MedicalExpensesService medicalExpensesService;


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
    public Page<PatientBean> queryPatient(String page, String pageSize, PatientBean patientBean) {
        if (StringUtils.isBlank(page)) {
            page = "1";
        }
        if (StringUtils.isBlank(pageSize)) {
            pageSize = "10";
        }
        Page<PatientBean> result = new Page<>(Long.valueOf(page), Long.valueOf(pageSize));
        return result.setRecords(patientDao.queryPatient(result, patientBean));
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
        if (map == null || map.get("patientId") == null || StringUtils.isBlank(String.valueOf(map.get("patientId"))) || map.get("paymenttime") == null || StringUtils.isBlank(String.valueOf(map.get("paymenttime"))))
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
    @Transactional(rollbackFor = MessageException.class)
    public PatientBean addPatient(PatientBean patientBean) throws MessageException {
        //查询患者信息
        PatientBean patient = queryPatientByHospNum(patientBean);

        String nowStr = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime());
        patientBean.setUpdateTime(nowStr);

        //如果患者出院，停止所有收费项目
        if ("0".equals(patientBean.getInHosp())) {
            paPayserviceDao.updatePaPayServiceByPatient(new PaPayserviceBean().setHospNum(patientBean.getHospNum()));
        }


        if (patient == null) {
//            patientBean.setHospTime(nowStr);
            //插入患者信息
            patientDao.addPatient(patientBean);
        } else {
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
        List<PatientBean> patientBeans = patientDao.queryPatient(patientBean);
        if (patientBeans.size() > 0 && patientBean.getIsStart() == 1) {
            for (int i = 0; i < patientBeans.size(); i++) {
                paPayserviceService.addAndUpdatePps(new PaPayserviceBean()
                        .setPayserviceId("42")
                        .setPatientId(patientBeans.get(i).getPatientId())
                        .setHospNum(patientBeans.get(i).getHospNum())
                        .setIsUse("1"));
            }
        } else if (patientBeans.size() > 0 && patientBean.getIsStart() == 0) {
            for (int i = 0; i < patientBeans.size(); i++) {
                paPayserviceDao.updatePaPayService(new PaPayserviceBean()
                        .setPayserviceId("42")
                        .setPatientId(patientBeans.get(i).getPatientId())
                        .setHospNum(patientBeans.get(i).getHospNum())
                        .setIsUse("0"));
            }
        }
    }

    @Override
    public Map<String,Object> patientArrears(PatientBean patientBean) throws Exception {
        Map<String,Object>  dataMap = new HashMap<>();
        Double medicalTatol=0.00d;
        List<MedicalExpensesBean> medicalExpensesBeans= medicalExpensesService.queryMedicalExpenses(new MedicalExpensesBean().setHospNum(patientBean.getHospNum()));
        Map<String, Object> paymentArrears = paymentService.queryGatherPaymentListInfo(new PatientBean().setHospNum(patientBean.getHospNum()).setIsMerge("1"));
        System.out.println(paymentArrears);
        if (medicalExpensesBeans.size()>0){
            for (int i=0;i<medicalExpensesBeans.size();i++){
                String depositFee = medicalExpensesBeans.get(i).getDepositFee();
                String arrearsFee = medicalExpensesBeans.get(i).getArrearsFee();
                String realFee = medicalExpensesBeans.get(i).getRealFee();
                if (StringUtils.isEmpty(depositFee)){
                    depositFee="0";
                }
                if (StringUtils.isEmpty(arrearsFee)){
                    arrearsFee="0";
                }
                if (StringUtils.isEmpty(realFee)){
                    realFee="0";
                }
                medicalTatol = medicalTatol + (Double.valueOf(depositFee) + Double.valueOf(arrearsFee) + Double.valueOf(realFee));
            }
        }
        dataMap.put("medicalTatol",medicalTatol*-1);
        dataMap.put("paymentArrears",paymentArrears.get("paymentTotal"));
        return dataMap;
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
                                paymentBean.setPaymenttime(paymenttime);
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
                    queryWrapper.eq(true, "PAYMENTTIME", pb.getPaymenttime());
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
