package com.jiubo.sam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.dao.PaymentDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.service.PayserviceService;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交费 服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentDao, PaymentBean> implements PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private PayserviceService payserviceService;

    @Override
    @Transactional
    public int addPayment(PaymentBean paymentBean) {
        return paymentDao.addPayment(paymentBean);
    }

    @Override
    public JSONObject queryGatherPayment(Map<String, Object> map) throws Exception {
        String comma = ",";
        JSONObject jsonObject = new JSONObject();
        PayserviceBean payserviceBean = new PayserviceBean();
        payserviceBean.setIsuse("1");
        List<PayserviceBean> payserviceBeans = payserviceService.queryPayservice(payserviceBean);
        jsonObject.put("payService", payserviceBeans);
        if (payserviceBeans != null && payserviceBeans.size() > 0) {
            StringBuffer buffHead = new StringBuffer();
            StringBuffer buffQianKun = new StringBuffer();
            StringBuffer buffer = new StringBuffer();

            buffHead.append("SELECT PATIENT_ID, NAME, SEX, HOSP_TIME, DEPT_ID, DEPTNAME, ACTUALPAYMENT,");
            buffer.append("SELECT A.PATIENT_ID,B.NAME,B.SEX,B.HOSP_TIME,B.DEPT_ID,C.NAME DEPTNAME,B.HOSP_NUM,SUM(A.ACTUALPAYMENT) ACTUALPAYMENT,");
            for (int i = 0; i < payserviceBeans.size(); i++) {
                PayserviceBean bean = payserviceBeans.get(i);
                String receivable = "RECEIVABLE_".concat(bean.getPayserviceId()).concat(comma);
                String shiJiao = "SHIJIAO_".concat(bean.getPayserviceId()).concat(comma);
                String begTime = "BEGTIME_".concat(bean.getPayserviceId()).concat(comma);
                String endTime = "ENDTIME_".concat(bean.getPayserviceId()).concat(comma);
                String paymentTime = "PAYMENTTIME_".concat(bean.getPayserviceId()).concat(comma);
                String qianKuan = "QIANKUAN_".concat(bean.getPayserviceId());

                if (i != payserviceBeans.size() - 1) {
                    buffQianKun.append(qianKuan).append("+");
                    qianKuan = qianKuan.concat(comma);
                }else{
                    buffQianKun.append(qianKuan).append(" QIANKUAN").append(" FROM (");
                }

                buffHead.append(receivable);
                buffHead.append(shiJiao);
                buffHead.append(begTime);
                buffHead.append(endTime);
                buffHead.append(paymentTime);
                buffHead.append(qianKuan);

                buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.RECEIVABLE ELSE 0 END) ").append(receivable);
                buffer.append("SUM(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.ACTUALPAYMENT ELSE 0 END) ").append(shiJiao);
                buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.BEGTIME ELSE NULL END) ").append(begTime);
                buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.ENDTIME ELSE NULL END) ").append(endTime);
                buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.PAYMENTTIME ELSE NULL END) ").append(paymentTime);
                buffer.append("(DATEDIFF(MONTH, MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId())
                        .append(" THEN A.ENDTIME ELSE NULL END), GETDATE()) + 1) * MAX(CASE A.PAYSERVICE_ID WHEN ")
                        .append(bean.getPayserviceId()).append(" THEN A.RECEIVABLE ELSE 0 END) ").append(qianKuan);
            }
            buffer.append(" FROM PAYMENT A,PATIENT B ");
            buffer.append(" LEFT JOIN DEPARTMENT C ");
            buffer.append(" ON C.DEPT_ID = B.DEPT_ID");
            buffer.append(" WHERE  A.PATIENT_ID = B.PATIENT_ID AND B.IN_HOSP = 1 ");
            if (map != null) {
                //患者姓名
                if (map.get("name") != null && StringUtils.isNotBlank(String.valueOf(map.get("name")))) {
                    buffer.append(" AND B.NAME = '").append(String.valueOf(map.get("name"))).append("'");
                }
                //科室
                if (map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
                    buffer.append(" AND B.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
                }
                //住院号
                if (map.get("hospNum") != null && StringUtils.isNotBlank(String.valueOf(map.get("hospNum")))) {
                    buffer.append(" AND B.HOSP_NUM = '").append(String.valueOf(map.get("deptId"))).append("'");
                }
                //入院日期
                if (map.get("begDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("begDate")))
                        && map.get("endDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endDate")))) {
                    buffer.append(" AND B.HOSP_TIME >= '").append(String.valueOf(map.get("begDate"))).append("'");
                    String endDate = String.valueOf(map.get("endDate"));
                    endDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.dateAdd(TimeUtil.parseAnyDate(endDate), TimeUtil.UNIT_DAY, 1));
                    buffer.append(" AND B.HOSP_TIME < '").append(endDate).append("'");
                }
            }
            buffer.append(" GROUP BY A.PATIENT_ID,B.NAME,B.SEX,B.HOSP_TIME,B.DEPT_ID,C.NAME,B.HOSP_NUM");
            if (map != null && map.get("qianKuan") != null && StringUtils.isNotBlank(String.valueOf(map.get("qianKuan")))) {
                buffer.append(" WHERE 1 = 1");
                String qianKuan = String.valueOf(map.get("qianKuan"));
                if ("1".equals(qianKuan)) {
                    buffer.append(" AND QIANKUAN > 0");
                } else if ("0".equals(qianKuan)) {
                    buffer.append(" AND QIANKUAN < 0");
                }
            }

            System.out.println(buffHead.append(comma).append(buffQianKun).append(buffer).append(") TAB "));
            jsonObject.put("payment", paymentDao.queryGatherPayment(buffer.toString()));
            return jsonObject;
        }
        return jsonObject;
    }

    @Override
    public List<PaymentBean> queryPaymentByPatientId(String patientId) {
        return paymentDao.queryPaymentByPatientId(patientId);
    }

    @Override
    public void addUpdatePayment(List<PaymentBean> list) throws MessageException {
        paymentDao.addUpdatePayment(list);
    }

    @Override
    public JSONObject queryPayment(Map<String, Object> map) throws Exception {
        String comma = ",";
        JSONObject jsonObject = new JSONObject();
        PayserviceBean payserviceBean = new PayserviceBean();
        payserviceBean.setIsuse("1");
        List<PayserviceBean> payserviceBeans = payserviceService.queryPayservice(payserviceBean);
        jsonObject.put("payService", payserviceBeans);
        if (payserviceBeans != null && payserviceBeans.size() > 0) {
            StringBuffer buffHead = new StringBuffer();
            StringBuffer buffQianKun = new StringBuffer();
            StringBuffer buffer = new StringBuffer();

            String index = payserviceBeans.get(0).getPayserviceId();

            buffer.append("SELECT P.DEPT_ID,DM.NAME,DM.ISUSE,P.HOSP_NUM,P.HOSP_TIME,P.NAME,P.IN_HOSP,P.AGE,P.OUT_HOSP,P.SEX");
            buffer.append(" ,PM"+index+".PAYMENT_ID, PM"+index+".PATIENT_ID, PM"+index+".PAYSERVICE_ID, PM"+index+".RECEIVABLE, PM"+index+".ACTUALPAYMENT, PM"+index+".BEGTIME, PM"+index+".ENDTIME, PM"+index+".PAYMENTTIME ");

            for (int i = 1 ; i < payserviceBeans.size() ; i++){
                String PM = "PM"+payserviceBeans.get(i).getPayserviceId();
                buffer.append(" ,"+PM+".PAYMENT_ID, "+PM+".PATIENT_ID, "+PM+".PAYSERVICE_ID, "+PM+".RECEIVABLE, "+PM+".ACTUALPAYMENT, "+PM+".BEGTIME, "+PM+".ENDTIME, "+PM+".PAYMENTTIME ");
            }

            buffer.append(" FROM ");

            buffer.append(" ( SELECT * FROM PAYMENT WHERE PAYSERVICE_ID = "+index+" ) PM"+index+" ");
            buffer.append(" LEFT JOIN PATIENT P ON P.PATIENT_ID=PM"+index+".PATIENT_ID ");
            buffer.append(" LEFT JOIN DEPARTMENT DM ON DM.DEPT_ID=P.DEPT_ID ");

            buffer.append(" LEFT JOIN PAYSERVICE PS0 ON PS0.PAYSERVICE_ID=PM"+index+".PAYSERVICE_ID");
            for (int i = 1 ; i < payserviceBeans.size() ; i++){
                String payserviceId = payserviceBeans.get(i).getPayserviceId();
                String PM = "PM"+payserviceId;
                buffer.append(" ,( SELECT * FROM PAYMENT WHERE PAYSERVICE_ID = "+payserviceBeans.get(i).getPayserviceId()+" ) "+PM+" ");
                buffer.append(" LEFT JOIN PAYSERVICE PS"+payserviceId+" ON PS"+payserviceId+".PAYSERVICE_ID="+PM+".PAYSERVICE_ID ");
            }

            if(payserviceBeans.size()>1){
                buffer.append(" WHERE");
            }
            for (int i = 1 ; i < payserviceBeans.size() ; i++){
                String payserviceId1 = payserviceBeans.get(i-1).getPayserviceId();
                String payserviceId2 = payserviceBeans.get(i).getPayserviceId();
                buffer.append(" PM"+payserviceId1+".PATIENT_ID = PM"+payserviceId2+".PATIENT_ID ");
                if(i != payserviceBeans.size()-1){
                    buffer.append(" AND ");
                }
            }


            System.out.println("buffer:"+buffer);
//            jsonObject.put("payment", paymentDao.queryGatherPayment(buffer.toString()));
            return jsonObject;
        }
        return jsonObject;
    }
}
