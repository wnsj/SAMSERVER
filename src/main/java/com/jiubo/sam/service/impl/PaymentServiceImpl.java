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
    public JSONObject queryGatherPayment(Map<String, Object> map) throws Exception {
        String comma = ",";
        JSONObject jsonObject = new JSONObject();
        PayserviceBean payserviceBean = new PayserviceBean();
        payserviceBean.setIsuse("1");
        List<PayserviceBean> payserviceBeans = payserviceService.queryPayservice(payserviceBean);
        jsonObject.put("payService", payserviceBeans);
        if (payserviceBeans != null && payserviceBeans.size() > 0) {
            StringBuffer bufferTAB = new StringBuffer();
            StringBuffer bufferG = new StringBuffer();
            StringBuffer bufferH = new StringBuffer();
            StringBuffer bufferQianKuan = new StringBuffer();
            bufferTAB.append("SELECT G.PATIENT_ID, G.NAME, G.SEX, G.HOSP_TIME, G.DEPT_ID, G.DEPTNAME, G.HOSP_NUM, G.ACTUALPAYMENT,");
            bufferG.append("(");
            bufferG.append("SELECT A.PATIENT_ID,B.NAME,B.SEX,B.HOSP_TIME,B.DEPT_ID,C.NAME DEPTNAME,B.HOSP_NUM,SUM(A.ACTUALPAYMENT) ACTUALPAYMENT,");
            bufferH.append("(");
            bufferH.append("SELECT E.PATIENT_ID,");
            for (int i = 0; i < payserviceBeans.size(); i++) {
                PayserviceBean bean = payserviceBeans.get(i);
                //String begTime = "BEGTIME_".concat(bean.getPayserviceId()).concat(comma);
                //String paymentTime = "PAYMENTTIME_".concat(bean.getPayserviceId()).concat(comma);
                String receivable = "RECEIVABLE_".concat(bean.getPayserviceId()).concat(comma);
                String shiJiao = "SHIJIAO_".concat(bean.getPayserviceId());
                String endTime = "ENDTIME_".concat(bean.getPayserviceId()).concat(comma);
                String qianKuan = "QIANKUAN_".concat(bean.getPayserviceId());
                bufferTAB.append("G.").append(shiJiao).append(comma);
                bufferTAB.append("H.").append(endTime);
                bufferTAB.append("H.").append(receivable);
                bufferTAB.append("H.").append(qianKuan).append(comma);
                bufferG.append("SUM(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.ACTUALPAYMENT ELSE 0 END) ").append(shiJiao);
                bufferH.append("MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.ENDTIME ELSE NULL END)  ").append(endTime);
                bufferH.append("MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.RECEIVABLE ELSE 0 END) ").append(receivable);
                bufferH.append("CASE WHEN CONVERT(VARCHAR(100), GETDATE(), 23) > MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN CONVERT(VARCHAR(100), E.ENDTIME, 23) ELSE NULL END) THEN ");
                bufferH.append("(DATEDIFF(MONTH, MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.ENDTIME ELSE NULL END), GETDATE())) * MAX(CASE E.PAYSERVICE_ID WHEN ")
                        .append(bean.getPayserviceId()).append(" THEN E.RECEIVABLE ELSE 0 END) ELSE 0 END ").append(qianKuan);

                if (i != payserviceBeans.size() - 1) {
                    bufferG.append(comma);
                    bufferH.append(comma);
                    bufferQianKuan.append("H.").append(qianKuan).append("+");
                } else {
                    bufferQianKuan.append("H.").append(qianKuan);
                }
            }
            bufferG.append(" FROM PAYMENT A,PATIENT B ");
            bufferG.append(" LEFT JOIN DEPARTMENT C ON C.DEPT_ID = B.DEPT_ID ");
            bufferG.append(" WHERE A.PATIENT_ID = B.PATIENT_ID AND B.IN_HOSP = 1");
            bufferG.append(" GROUP BY A.PATIENT_ID,B.NAME,B.SEX,B.HOSP_TIME,B.DEPT_ID,C.NAME,B.HOSP_NUM ");
            bufferG.append(" ) G");

            bufferH.append(" FROM ");
            bufferH.append("(");
            bufferH.append(" SELECT PATIENT_ID,MAX(ENDTIME) ENDTIME,PAYSERVICE_ID FROM PAYMENT GROUP BY PATIENT_ID,PAYSERVICE_ID");
            bufferH.append(" ) D, PAYMENT E");
            bufferH.append(" WHERE  D.PATIENT_ID = E.PATIENT_ID AND D.ENDTIME = E.ENDTIME AND D.PAYSERVICE_ID = E.PAYSERVICE_ID GROUP BY E.PATIENT_ID");
            bufferH.append(" ) H");

            bufferTAB.append(bufferQianKuan);
            bufferTAB.append(" AS QIANKUAN");
            bufferTAB.append(" FROM ");
            bufferTAB.append(bufferG);
            bufferTAB.append(comma);
            bufferTAB.append(bufferH);
            bufferTAB.append(" WHERE G.PATIENT_ID = H.PATIENT_ID");
            if (map != null) {
                //患者姓名
                if (map.get("name") != null && StringUtils.isNotBlank(String.valueOf(map.get("name")))) {
                    bufferTAB.append(" AND G.NAME = '").append(String.valueOf(map.get("name"))).append("'");
                }
                //科室
                if (map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
                    bufferTAB.append(" AND G.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
                }
                //住院号
                if (map.get("hospNum") != null && StringUtils.isNotBlank(String.valueOf(map.get("hospNum")))) {
                    bufferTAB.append(" AND G.HOSP_NUM = '").append(String.valueOf(map.get("deptId"))).append("'");
                }
                //入院日期
                if (map.get("begDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("begDate")))
                        && map.get("endDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endDate")))) {
                    bufferTAB.append(" AND G.HOSP_TIME >= '").append(String.valueOf(map.get("begDate"))).append("'");
                    String endDate = String.valueOf(map.get("endDate"));
                    endDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.dateAdd(TimeUtil.parseAnyDate(endDate), TimeUtil.UNIT_DAY, 1));
                    bufferTAB.append(" AND G.HOSP_TIME < '").append(endDate).append("'");
                }
                //是否欠款（1：欠，0：不欠）
                String qianKuan = String.valueOf(map.get("qianKuan"));
                if ("1".equals(qianKuan)) {
                    bufferTAB.append(" AND QIANKUAN > 0");
                } else if ("0".equals(qianKuan)) {
                    bufferTAB.append(" AND QIANKUAN < 0");
                }
                //System.out.println(bufferG.toString());
                //System.out.println(bufferH.toString());
                //System.out.println(bufferTAB.toString());
                jsonObject.put("payment", paymentDao.queryGatherPayment(bufferTAB.toString()));
            }
        }
        return jsonObject;
    }

    @Override
    public List<PaymentBean> queryPaymentByPatientId(String patientId) {
        return paymentDao.queryPaymentByPatientId(patientId);
    }

    @Override
    public void addPayment(List<PaymentBean> list) throws MessageException {
        paymentDao.addPayment(list);
    }

    @Override
    public void updatePayment(List<PaymentBean> list) throws MessageException {
        paymentDao.updatePayment(list);
    }
}

//{
//        StringBuffer buffHead = new StringBuffer();
//        StringBuffer buffQianKun = new StringBuffer();
//        StringBuffer buffer = new StringBuffer();
//
//        buffHead.append("SELECT PATIENT_ID, NAME, SEX, HOSP_TIME, DEPT_ID, DEPTNAME, ACTUALPAYMENT,");
//        buffer.append("SELECT A.PATIENT_ID,B.NAME,B.SEX,B.HOSP_TIME,B.DEPT_ID,C.NAME DEPTNAME,B.HOSP_NUM,SUM(A.ACTUALPAYMENT) ACTUALPAYMENT,");
//        for (int i = 0; i < payserviceBeans.size(); i++) {
//        PayserviceBean bean = payserviceBeans.get(i);
//        String receivable = "RECEIVABLE_".concat(bean.getPayserviceId()).concat(comma);
//        String shiJiao = "SHIJIAO_".concat(bean.getPayserviceId()).concat(comma);
//        String begTime = "BEGTIME_".concat(bean.getPayserviceId()).concat(comma);
//        String endTime = "ENDTIME_".concat(bean.getPayserviceId()).concat(comma);
//        String paymentTime = "PAYMENTTIME_".concat(bean.getPayserviceId()).concat(comma);
//        String qianKuan = "QIANKUAN_".concat(bean.getPayserviceId());
//
//        if (i != payserviceBeans.size() - 1) {
//        buffQianKun.append(qianKuan).append("+");
//        qianKuan = qianKuan.concat(comma);
//        }else{
//        buffQianKun.append(qianKuan).append(" QIANKUAN").append(" FROM (");
//        }
//
//        buffHead.append(receivable);
//        buffHead.append(shiJiao);
//        buffHead.append(begTime);
//        buffHead.append(endTime);
//        buffHead.append(paymentTime);
//        buffHead.append(qianKuan);
//
//        buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.RECEIVABLE ELSE 0 END) ").append(receivable);
//        buffer.append("SUM(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.ACTUALPAYMENT ELSE 0 END) ").append(shiJiao);
//        buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.BEGTIME ELSE NULL END) ").append(begTime);
//        buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.ENDTIME ELSE NULL END) ").append(endTime);
//        buffer.append("MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.PAYMENTTIME ELSE NULL END) ").append(paymentTime);
//        buffer.append("(DATEDIFF(MONTH, MAX(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId())
//        .append(" THEN A.ENDTIME ELSE NULL END), GETDATE()) + 1) * MAX(CASE A.PAYSERVICE_ID WHEN ")
//        .append(bean.getPayserviceId()).append(" THEN A.RECEIVABLE ELSE 0 END) ").append(qianKuan);
//        }
//        buffer.append(" FROM PAYMENT A,PATIENT B ");
//        buffer.append(" LEFT JOIN DEPARTMENT C ");
//        buffer.append(" ON C.DEPT_ID = B.DEPT_ID");
//        buffer.append(" WHERE  A.PATIENT_ID = B.PATIENT_ID AND B.IN_HOSP = 1 ");
//        if (map != null) {
//        //患者姓名
//        if (map.get("name") != null && StringUtils.isNotBlank(String.valueOf(map.get("name")))) {
//        buffer.append(" AND B.NAME = '").append(String.valueOf(map.get("name"))).append("'");
//        }
//        //科室
//        if (map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
//        buffer.append(" AND B.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
//        }
//        //住院号
//        if (map.get("hospNum") != null && StringUtils.isNotBlank(String.valueOf(map.get("hospNum")))) {
//        buffer.append(" AND B.HOSP_NUM = '").append(String.valueOf(map.get("deptId"))).append("'");
//        }
//        //入院日期
//        if (map.get("begDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("begDate")))
//        && map.get("endDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endDate")))) {
//        buffer.append(" AND B.HOSP_TIME >= '").append(String.valueOf(map.get("begDate"))).append("'");
//        String endDate = String.valueOf(map.get("endDate"));
//        endDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.dateAdd(TimeUtil.parseAnyDate(endDate), TimeUtil.UNIT_DAY, 1));
//        buffer.append(" AND B.HOSP_TIME < '").append(endDate).append("'");
//        }
//        }
//        buffer.append(" GROUP BY A.PATIENT_ID,B.NAME,B.SEX,B.HOSP_TIME,B.DEPT_ID,C.NAME,B.HOSP_NUM");
//        if (map != null && map.get("qianKuan") != null && StringUtils.isNotBlank(String.valueOf(map.get("qianKuan")))) {
//        buffer.append(" WHERE 1 = 1");
//        String qianKuan = String.valueOf(map.get("qianKuan"));
//        if ("1".equals(qianKuan)) {
//        buffer.append(" AND QIANKUAN > 0");
//        } else if ("0".equals(qianKuan)) {
//        buffer.append(" AND QIANKUAN < 0");
//        }
//        }
//
//        System.out.println(buffHead.append(comma).append(buffQianKun).append(buffer).append(") TAB "));
//        jsonObject.put("payment", paymentDao.queryGatherPayment(buffer.toString()));
//        return jsonObject;
//        }