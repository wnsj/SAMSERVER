package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.DepartmentBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.DepartmentService;
import com.jiubo.sam.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.service.PaymentService;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public void queryPatientByHospNum(PatientBean patientBean) throws MessageException {
        QueryWrapper<PatientBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "HOSP_NUM", patientBean.getHospNum());
        //queryWrapper.select("PATIENT_ID", "HOSP_NUM", "NAME");
        queryWrapper.select("*");
        List<PatientBean> patientBeans = patientDao.selectList(queryWrapper);
        if (patientBeans.size() > 0) throw new MessageException("住院号为" + patientBean.getHospNum() + "患者已存在!");
    }

    @Override
    @Transactional
    public void addPatient(PatientBean patientBean) throws MessageException {
        //查询患者信息
        queryPatientByHospNum(patientBean);

        String nowStr = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime());
        patientBean.setHospTime(nowStr);
        patientBean.setUpdateTime(nowStr);
        //插入患者信息
        patientDao.addPatient(patientBean);

        if (patientBean.getPaymentList() != null && patientBean.getPaymentList().size() > 0) {
            for (PaymentBean paymentBean : patientBean.getPaymentList()) {
                paymentBean.setPatientId(patientBean.getPatientId());
                paymentBean.setUpdatetime(nowStr);
                //插入交费信息
                paymentService.addPayment(paymentBean);
                //插入患者、收费项目、交费记录关系表
                patientDao.addPatientPayservicePayment(patientBean.getPatientId(), paymentBean.getPayserviceId(), paymentBean.getPaymentId());
            }
        }
    }

    @Override
    public void addPatientList(Map<Object, Object> map) throws Exception {
        Map<String,DepartmentBean> deptMap = new HashMap<String,DepartmentBean>();
        List<PatientBean> patientBeans = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "##" + entry.getValue());
            PatientBean patientBean = new PatientBean();
            List list = (List) entry.getValue();
            if (list == null || list.size() <= 0 || entry.getValue() == null) continue;
            for (int i = 0; i < list.size(); i++) {
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
                        if(list.get(4) != null){
                            String dpetName = String.valueOf(list.get(4));
                            if(StringUtils.isNotBlank(dpetName)){
                                DepartmentBean bean = deptMap.get(dpetName);
                                if(bean != null){
                                    patientBean.setDeptId(bean.getDeptId());
                                }else {
                                    bean = new DepartmentBean();
                                    bean.setName(dpetName);
                                    List<DepartmentBean> departmentBeans = departmentService.queryDeptByName(bean);
                                    if(departmentBeans != null && departmentBeans.size() > 0){
                                        patientBean.setDeptId(departmentBeans.get(0).getDeptId());
                                        deptMap.put(dpetName,departmentBeans.get(0));
                                    }
                                }
                            }
                        }
                        break;
                    case 5:
                        if(list.get(5) != null){
                            String inHosp = String.valueOf(list.get(5));
                            if("在".equals(inHosp) || "是".equals(inHosp)){
                                inHosp = "1";
                            }else{
                                inHosp = "0";
                            }
                            patientBean.setInHosp(inHosp);
                        }
                        break;
                    case 6:
                        if (list.get(6) != null && StringUtils.isNotBlank(String.valueOf(list.get(6))))
                            patientBean.setHospTime(TimeUtil.getDateYYYY_MM_DD((Date) list.get(6)));
                        break;
                    case 7:
                        if (list.get(7) != null && StringUtils.isNotBlank(String.valueOf(list.get(7))))
                            patientBean.setOutHosp(TimeUtil.getDateYYYY_MM_DD((Date) list.get(7)));
                        break;
                }
            }
            patientBeans.add(patientBean);
        }

        patientDao.saveOrUpdate(patientBeans);
    }
}
