package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.util.TimeUtil;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.ClientInfoStatus;
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
    private PaymentService paymentService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PatienttypeService patienttypeService;

    @Autowired
    private MedicinsurtypeService medicinsurtypeService;

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
            bean.setPaymentList(paymentBeans);
        }
        return bean;
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

    public PatientBean fuzzyQuery(PatientBean patientBean) {
        PatientBean bean = new PatientBean();
        List<PatientBean> pbList = new ArrayList<PatientBean>();
        List<PaymentBean> paymentBeans = new ArrayList<PaymentBean>();
        pbList = patientDao.fuzzyQuery(patientBean);
        if (pbList.size() > 0) {
            bean = pbList.get(0);
        }
        if (bean != null) {
            //查询所有的收费项目
            paymentBeans = paymentService.queryPaymentByPatientId(bean.getPatientId());
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
        bean.setPaymentList(paymentService.queryPaymentByPatientIdTime(map));
        return bean;
    }

    @Override
    @Transactional
    public void addPatient(PatientBean patientBean) throws MessageException {
        //查询患者信息
        PatientBean patient = queryPatientByHospNum(patientBean);

        String nowStr = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime());
        patientBean.setUpdateTime(nowStr);

        if (patient == null) {
            patientBean.setHospTime(nowStr);
            //插入患者信息
            patientDao.addPatient(patientBean);
        } else {
            List<PatientBean> patientBeans = new ArrayList<>();
            patientBeans.add(patientBean);
            //修改患者信息
            patientDao.saveOrUpdate(patientBeans);
        }


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
    public void addPatientList(Map<Object, Object> map) throws Exception {
        Map<String, DepartmentBean> deptMap = new HashMap<String, DepartmentBean>();
        Map<String, PatienttypeBean> patientTypeMap = new HashMap<String, PatienttypeBean>();
        Map<String, MedicinsurtypeBean> miTypeMap = new HashMap<String, MedicinsurtypeBean>();

        List<PatientBean> patientBeans = new ArrayList<PatientBean>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + "##" + entry.getValue());
            PatientBean patientBean = new PatientBean();
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
                        if (list.get(7) != null && StringUtils.isNotBlank(String.valueOf(list.get(7))))
                            patientBean.setOutHosp(TimeUtil.getDateYYYY_MM_DD((Date) list.get(7)));
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
        for (List<PatientBean> list : lists) {
            patientDao.saveOrUpdate(list);
        }
    }

    @Override
    @Transactional
    public List<PatientBean> queryPatientListByHospNum(Map<Object, Object> map) throws ParseException, Exception {
        List<PatientBean> patientList = new ArrayList<PatientBean>();

        List<PaymentBean> paymentBeanList = new ArrayList<PaymentBean>();


        for (int j = 2; j < map.size() + 2; j++) {
            PatientBean patientBean = new PatientBean();
            PaymentBean paymentBean = new PaymentBean();
            List list = (List) map.get(j);
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
                    patientList.add(patientBean);
                    break;
                } else {
                    paymentBean.setPatientId(bean.getPatientId());
                    paymentBean.setIsuse(true);
                    switch (i) {
                        case 2:
                            if (list.get(2) != null && StringUtils.isNotBlank(String.valueOf(list.get(2))))
                                paymentBean.setPaymenttime(TimeUtil.getDateYYYY_MM_DD((Date) list.get(2)));
                            break;
                        case 3:
                            if (list.get(3) != null && StringUtils.isNotBlank(String.valueOf(list.get(3)))) {
                                payserviceId = Double.valueOf(String.valueOf(list.get(3))).intValue();
                                System.out.println("项目ID：" + payserviceId);
                                paymentBean.setPayserviceId(String.valueOf(payserviceId));
                            }
                            break;
                        case 5:
                            if (list.get(5) != null && StringUtils.isNotBlank(String.valueOf(list.get(5))))
                                paymentBean.setReceivable(Double.valueOf(String.valueOf(list.get(5))));
                            break;
                        case 6:
                            if (list.get(6) != null && StringUtils.isNotBlank(String.valueOf(list.get(6))))
                                paymentBean.setActualpayment(Double.valueOf(String.valueOf(list.get(5))));
                            break;
                        case 7:
                            if (list.get(7) != null && StringUtils.isNotBlank(String.valueOf(list.get(7))))
                                paymentBean.setBegtime(TimeUtil.getDateYYYY_MM_DD((Date) list.get(7)));
                            break;
                        case 8:
                            if (list.get(8) != null && StringUtils.isNotBlank(String.valueOf(list.get(8))))
                                paymentBean.setEndtime(TimeUtil.getDateYYYY_MM_DD((Date) list.get(8)));
                            break;
                    }
                }
            }
            paymentBeanList.add(paymentBean);
        }

        for (int i = 0; i < patientList.size(); i++) {
            System.out.println("患者住院号个数：" + patientList.get(i).getHospNum());
        }
        if (patientList == null || patientList.size() <= 0) {
            //分批处理
            List<List<PaymentBean>> lists = splitList(paymentBeanList, 100);
            for (List<PaymentBean> pBean : lists) {
                paymentService.addPayment(pBean);
            }
        }
        return patientList;
    }

    //分批处理
    public static <T> List<List<T>> splitList(List<T> list, int items) {
        List<List<T>> lists = new ArrayList<List<T>>();
        if (list == null && list.size() <= 0) return lists;
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
