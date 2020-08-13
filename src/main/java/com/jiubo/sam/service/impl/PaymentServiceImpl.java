package com.jiubo.sam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.dao.PaymentDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.service.PayserviceService;
import com.jiubo.sam.util.TimeUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
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
            bufferTAB.append("SELECT G.PATIENT_ID, G.NAME , G.IN_HOSP, G.SEX, G.AGE,G.HOSP_TIME, G.DEPT_ID, G.DEPTNAME, G.HOSP_NUM, G.ACTUALPAYMENT,G.PATITYPEID, G.MITYPEID,G.PATITYPENAME,G.MITYPENAME,");
            bufferG.append("(");
            bufferG.append("SELECT A.PATIENT_ID,B.NAME,B.IN_HOSP,B.SEX,B.AGE,B.HOSP_TIME,B.DEPT_ID,C.NAME DEPTNAME,B.HOSP_NUM,SUM(A.ACTUALPAYMENT) ACTUALPAYMENT,B.PATITYPEID, B.MITYPEID,PATIENTTYPE.PATITYPENAME,MEDICINSURTYPE.MITYPENAME,");
            bufferH.append("(");
            bufferH.append("SELECT E.PATIENT_ID,");
            for (int i = 0; i < payserviceBeans.size(); i++) {
                PayserviceBean bean = payserviceBeans.get(i);
                String receivable = "RECEIVABLE_".concat(bean.getPayserviceId()).concat(comma);
                String shiJiao = "SHIJIAO_".concat(bean.getPayserviceId());
                String endTime = "ENDTIME_".concat(bean.getPayserviceId()).concat(comma);
                String qianKuan = "QIANKUAN_".concat(bean.getPayserviceId());
                String price = "PRICE_".concat(bean.getPayserviceId()).concat(comma);
                bufferTAB.append("G.").append(shiJiao).append(comma);
                bufferTAB.append("H.").append(endTime);
                bufferTAB.append("H.").append(receivable);
                bufferTAB.append("H.").append(price);
                bufferTAB.append("H.").append(qianKuan).append(comma);
                bufferG.append("SUM(CASE A.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN A.ACTUALPAYMENT ELSE 0 END) ").append(shiJiao);
                bufferH.append("MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.ENDTIME ELSE NULL END)  ").append(endTime);
                bufferH.append("MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.RECEIVABLE ELSE 0 END) ").append(receivable);
                bufferH.append("MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.PRICE ELSE 0 END ) ").append(price);

//                bufferH.append(" CASE WHEN CONVERT(VARCHAR(100), GETDATE(), 23) > MAX(CASE E.PAYSERVICE_ID WHEN  ").append(bean.getPayserviceId()).append(" THEN CONVERT(VARCHAR(100), E.ENDTIME, 23) ELSE NULL END) ")
//                        .append(" THEN CASE WHEN (DATEDIFF(MONTH, MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.ENDTIME ELSE NULL END), GETDATE())) = 0 THEN 1 ")
//                        .append(" ELSE DATEDIFF(MONTH, MAX(CASE E.PAYSERVICE_ID WHEN  ").append(bean.getPayserviceId()).append(" THEN E.ENDTIME ELSE NULL END), GETDATE()) END")
//                        .append(" * MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.RECEIVABLE ELSE 0 END) ELSE 0 END ").append(qianKuan);
                bufferH.append(" CASE WHEN CONVERT(VARCHAR(100), GETDATE(), 23) > MAX(CASE E.PAYSERVICE_ID WHEN 40 THEN CONVERT(VARCHAR(100), E.ENDTIME, 23) ELSE NULL END)")
                        .append(" THEN DATEDIFF(DAY, MAX(CASE E.PAYSERVICE_ID WHEN 40 THEN E.ENDTIME ELSE NULL END), GETDATE()) * MAX(CASE E.PAYSERVICE_ID WHEN 40 THEN E.PRICE ELSE 0 END) ELSE 0 END ")
                        .append(qianKuan);

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
            bufferG.append(" LEFT JOIN PATIENTTYPE ");
            bufferG.append(" ON PATIENTTYPE.PATITYPEID = B.PATITYPEID");
            bufferG.append(" LEFT JOIN MEDICINSURTYPE ");
            bufferG.append(" ON MEDICINSURTYPE.MITYPEID = B.MITYPEID");
            bufferG.append(" WHERE A.PATIENT_ID = B.PATIENT_ID AND B.IN_HOSP = 1");
            bufferG.append(" GROUP BY A.PATIENT_ID,B.NAME,B.IN_HOSP,B.SEX,B.HOSP_TIME,B.DEPT_ID,C.NAME,B.HOSP_NUM,B.AGE,B.PATITYPEID, B.MITYPEID,PATIENTTYPE.PATITYPENAME,MEDICINSURTYPE.MITYPENAME");
            bufferG.append(" ) G");

            bufferH.append(" FROM ");
            bufferH.append("(");
            bufferH.append(" SELECT PAYMENT.PATIENT_ID,MAX(PAYMENT.ENDTIME) ENDTIME,PAYMENT.PAYSERVICE_ID")
                    .append(" FROM PAYMENT,( SELECT PATIENT_ID,MAX(PAYMENTTIME) PAYMENTTIME FROM PAYMENT WHERE ISUSE = 1 GROUP BY PATIENT_ID ) PAYM")
                    .append(" WHERE PAYMENT.PATIENT_ID = PAYM.PATIENT_ID AND PAYMENT.PAYMENTTIME = PAYM.PAYMENTTIME ")
                    .append(" GROUP BY PAYMENT.PATIENT_ID,PAYMENT.PAYSERVICE_ID");
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
                    bufferTAB.append(" AND G.NAME LIKE '%").append(String.valueOf(map.get("name"))).append("%'");
                }
                //科室
                if (map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
                    bufferTAB.append(" AND G.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
                }
                //住院号
                if (map.get("hospNum") != null && StringUtils.isNotBlank(String.valueOf(map.get("hospNum")))) {
                    bufferTAB.append(" AND G.HOSP_NUM LIKE '%").append(String.valueOf(map.get("hospNum"))).append("%'");
                }
                //入院日期
                if (map.get("begDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("begDate")))
                        && map.get("endDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endDate")))) {
                    bufferTAB.append(" AND G.HOSP_TIME >= '").append(String.valueOf(map.get("begDate"))).append("'");
                    String endDate = String.valueOf(map.get("endDate"));
                    endDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.dateAdd(TimeUtil.parseAnyDate(endDate), TimeUtil.UNIT_DAY, 1));
                    bufferTAB.append(" AND G.HOSP_TIME < '").append(endDate).append("'");
                }
                //患者类型
                if (map.get("patitypeid") != null && StringUtils.isNotBlank(String.valueOf(map.get("patitypeid")))) {
                    bufferTAB.append(" AND G.PATITYPEID = '").append(String.valueOf(map.get("patitypeid"))).append("'");
                }
                //医保类型
                if (map.get("mitypeid") != null && StringUtils.isNotBlank(String.valueOf(map.get("mitypeid")))) {
                    bufferTAB.append(" AND G.MITYPEID = '").append(String.valueOf(map.get("mitypeid"))).append("'");
                }
                //是否欠款（1：欠，0：不欠）
                if (map.get("qianKuan") != null && StringUtils.isNotBlank(String.valueOf(map.get("qianKuan")))) {
                    String qianKuan = String.valueOf(map.get("qianKuan"));
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("SELECT * FROM ( ").append(bufferTAB).append(" ) Z");
                    if ("1".equals(qianKuan)) {
                        stringBuffer.append(" WHERE Z.QIANKUAN > 0");
                    } else if ("0".equals(qianKuan)) {
                        stringBuffer.append(" WHERE Z.QIANKUAN <= 0");
                    }
                    bufferTAB = stringBuffer;
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
    public List<PaymentBean> queryNewByPatientIdPayment(PaymentBean paymentBean) throws Exception {
        return paymentDao.queryNewByPatientIdPayment(paymentBean);
    }


    @Override
    public JSONObject queryPaymentList(Map<String, Object> map) throws MessageException, Exception {
        String comma = ",";
        JSONObject jsonObject = new JSONObject();
        PayserviceBean payserviceBean = new PayserviceBean();
        payserviceBean.setIsuse("1");
        List<PayserviceBean> payserviceBeans = payserviceService.queryPayservice(payserviceBean);
        jsonObject.put("payService", payserviceBeans);
        if (payserviceBeans != null && payserviceBeans.size() > 0) {
            StringBuffer bufferD = new StringBuffer();
            StringBuffer bufferA = new StringBuffer();
            StringBuffer bufferTAB = new StringBuffer();
            StringBuffer bufferActualpayment = new StringBuffer();
            bufferD.append("SELECT D.*,C.*,PPP.*,DATEDIFF(day, PPP.ENDDATE, GETDATE()) AS DAY_NUM,E.NAME DEPTNAME,F.PATITYPENAME,G.MITYPENAME,H.ACCOUNT_NAME ACCNAME FROM (");
            bufferA.append("SELECT A.PATIENT_ID,B.DEPT_ID,A.PAYMENTTIME,MAX(A.PRICE) PRICE,MAX(A.DAYS) DAYS,A.ACCOUNT_ID,");
            // 孙云龙修改 （由于需要记录历史记录 所以 查的是缴费表里的科室id 而不是【原逻辑】=>患者表里的）修改处 查询 添加 TAB.DEPT_ID
            bufferTAB.append("SELECT TAB.PATIENT_ID,TAB.DEPT_ID,TAB.PAYMENTTIME,MAX(TAB.PRICE) PRICE,MAX(TAB.DAYS) DAYS,TAB.ACCOUNT_ID ACCID,");
            // end
            for (int i = 0; i < payserviceBeans.size(); i++) {
                PayserviceBean bean = payserviceBeans.get(i);
                String receivable = "RECEIVABLE_".concat(bean.getPayserviceId());
                String actualPayment = "ACTUALPAYMENT_".concat(bean.getPayserviceId());
                String begTime = "BEGTIME_".concat(bean.getPayserviceId());
                String endTime = "ENDTIME_".concat(bean.getPayserviceId());
                String price = "PRICE_".concat(bean.getPayserviceId());
                bufferA.append("CASE A.PAYSERVICE_ID  WHEN ").append(bean.getPayserviceId()).append(" THEN SUM(A.RECEIVABLE) ELSE 0 END ").append(receivable).append(comma);
                bufferA.append("CASE A.PAYSERVICE_ID  WHEN ").append(bean.getPayserviceId()).append(" THEN SUM(A.ACTUALPAYMENT) ELSE 0 END ").append(actualPayment).append(comma);
                bufferA.append("CASE A.PAYSERVICE_ID  WHEN ").append(bean.getPayserviceId()).append(" THEN MAX(A.PRICE) ELSE 0 END ").append(price).append(comma);
                bufferA.append("CASE A.PAYSERVICE_ID  WHEN ").append(bean.getPayserviceId()).append(" THEN MAX(A.BEGTIME) ELSE NULL END ").append(begTime).append(comma);
                bufferA.append("CASE A.PAYSERVICE_ID  WHEN ").append(bean.getPayserviceId()).append(" THEN MAX(A.ENDTIME) ELSE NULL END ").append(endTime);

                bufferTAB.append("MAX( ").append(begTime).append(" ) ").append(begTime).append(comma);
                bufferTAB.append("MAX( ").append(endTime).append(" ) ").append(endTime).append(comma);
                bufferTAB.append("MAX( ").append(price).append(" )").append(price).append(comma);
                bufferTAB.append("SUM( ").append(receivable).append(" ) ").append(receivable).append(comma);
                bufferTAB.append("SUM( ").append(actualPayment).append(" ) ").append(actualPayment).append(comma);

                bufferActualpayment.append("SUM( ").append(actualPayment).append(" )");
                if (i != payserviceBeans.size() - 1) {
                    bufferA.append(comma);
                    bufferActualpayment.append("+");
                }
            }
            // 孙云龙修改 （由于需要记录历史记录 所以 查的是缴费表里的科室id 而不是【原逻辑】=>患者表里的）修改处 查询 添加 DEPT_ID
            bufferA.append(" FROM PAYMENT A,( SELECT PATIENT_ID,DEPT_ID,PAYMENTTIME FROM PAYMENT ");
            // end
            if (map != null && map.get("begDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("begDate")))
                    && map.get("endDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endDate")))) {
                bufferA.append(" WHERE ");
                bufferA.append(" PAYMENTTIME >= '").append(String.valueOf(map.get("begDate"))).append("'");
                String endDate = String.valueOf(map.get("endDate"));
                endDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.dateAdd(TimeUtil.parseAnyDate(endDate), TimeUtil.UNIT_DAY, 1));
                bufferA.append(" AND PAYMENTTIME < '").append(endDate).append("'");
            }
            bufferA.append(" GROUP BY PATIENT_ID,DEPT_ID,PAYMENTTIME ) B");
            bufferA.append(" WHERE A.PATIENT_ID = B.PATIENT_ID AND A.PAYMENTTIME = B.PAYMENTTIME");
            // 孙云龙修改 查询条件deptId 改为 缴费表里的deptId
            // 科室条件查询
            if (null != map && map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
                bufferA.append(" AND B.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
            }
            // 由于多查了字段 科室id 所以分组条件中需添加 B.DEPT_ID
            bufferA.append(" GROUP BY A.PATIENT_ID,B.DEPT_ID,A.PAYSERVICE_ID,A.PAYMENTTIME,A.ACCOUNT_ID ");
            // end
            bufferTAB.append(bufferActualpayment);
            bufferTAB.append(" ACTUALPAYMENT FROM (");
            bufferTAB.append(bufferA);
            // 孙云龙修改 由于多查了字段 科室id 所以分组条件中需添加 TAB.DEPT_ID
            bufferTAB.append(" ) TAB GROUP BY TAB.PATIENT_ID,TAB.DEPT_ID,TAB.PAYMENTTIME,TAB.ACCOUNT_ID");
            // end
            bufferD.append(bufferTAB);
            //bufferD.append(" ) D,PATIENT C,DEPARTMENT E,PATIENTTYPE F,MEDICINSURTYPE G");
            // 孙云龙修改 在此处 D表 关联 科室表 查出 科室名字（因为科室查询条件已经在D表中）
            bufferD.append(" ) D LEFT JOIN ACCOUNT H ON H.ACCOUNT_ID = D.ACCID LEFT JOIN DEPARTMENT E ON D.DEPT_ID = E.DEPT_ID,PATIENT C");
//            bufferD.append(" LEFT JOIN DEPARTMENT E ON  C.DEPT_ID = E.DEPT_ID");
            // end
            bufferD.append(" LEFT JOIN PATIENTTYPE F ON C.PATITYPEID = F.PATITYPEID");
            bufferD.append(" LEFT JOIN MEDICINSURTYPE G ON C.MITYPEID = G.MITYPEID");
            //添加结束时间
            bufferD.append(" LEFT JOIN \n" +
                    "\t\t(SELECT PP.PATIENT_ID,MAX(PP.ENDTIME) FROM\n" +
                    "\t(SELECT P.PATIENT_ID,P.ENDTIME FROM PAYMENT P WHERE ENDTIME IS NOT NULL GROUP BY P.PATIENT_ID,P.ENDTIME )PP GROUP BY PP.PATIENT_ID ) AS PPP (PATIENT_ID,ENDDATE) ON PPP.PATIENT_ID=C.PATIENT_ID");
            //bufferD.append(" WHERE D.PATIENT_ID = C.PATIENT_ID AND C.DEPT_ID = E.DEPT_ID AND C.PATITYPEID=F.PATITYPEID AND C.MITYPEID=G.MITYPEID");
            bufferD.append(" WHERE D.PATIENT_ID = C.PATIENT_ID ");
            if (map != null && map.get("endBegDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endBegDate")))
                    && map.get("endEndDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endEndDate")))) {
                //System.out.println("时间：");
                bufferD.append(" AND PPP.ENDDATE >= '").append(String.valueOf(map.get("endBegDate"))).append("'");
                String endDate = String.valueOf(map.get("endEndDate"));
                endDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.dateAdd(TimeUtil.parseAnyDate(endDate), TimeUtil.UNIT_DAY, 1));
                bufferD.append(" AND PPP.ENDDATE < '").append(endDate).append("'");
            }
            if (map != null) {
                //患者姓名
                if (map.get("name") != null && StringUtils.isNotBlank(String.valueOf(map.get("name")))) {
                    bufferD.append(" AND C.NAME LIKE '%").append(String.valueOf(map.get("name"))).append("%'");
                }
                // 孙云龙修改 （当患者转科室时 缴费需随患者科室变化而变化 但要有历史记录【如：患者有科一 转到 科二 那么缴费记录 应该有科一和科二的 而不是只有科二】）
                //科室
//                if (map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
//                    bufferD.append(" AND C.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
//                }
                // end
                //住院号
                if (map.get("hospNum") != null && StringUtils.isNotBlank(String.valueOf(map.get("hospNum")))) {
                    //bufferD.append(" AND C.HOSP_NUM LIKE '%").append(String.valueOf(map.get("hospNum"))).append("%'");
                    bufferD.append(" AND C.HOSP_NUM = '").append(String.valueOf(map.get("hospNum"))).append("'");
                }
                //性别
                if (map.get("sex") != null && StringUtils.isNotBlank(String.valueOf(map.get("sex")))) {
                    String sex = String.valueOf(map.get("sex"));
                    if ("1".equals(sex) || "男".equals(sex)) {
                        //男
                        bufferD.append(" AND C.SEX = '1'");
                    } else if ("2".equals(sex) || "女".equals(sex)) {
                        //女
                        bufferD.append(" AND C.SEX = '2'");
                    }
                }
                //患者类型
                if (map.get("patitypeid") != null && StringUtils.isNotBlank(String.valueOf(map.get("patitypeid")))) {
                    bufferD.append(" AND C.PATITYPEID = '").append(String.valueOf(map.get("patitypeid"))).append("'");
                }
                //社保类型
                if (map.get("mitypeid") != null && StringUtils.isNotBlank(String.valueOf(map.get("mitypeid")))) {
                    bufferD.append(" AND C.MITYPEID = '").append(String.valueOf(map.get("mitypeid"))).append("'");
                }
                //是否在院
                if (map.get("inHosp") != null && StringUtils.isNotBlank(String.valueOf(map.get("inHosp")))){
                    bufferD.append(" AND C.IN_HOSP = '").append(String.valueOf(map.get("inHosp"))).append("'");
                }
            }

            //System.out.println(bufferD.toString());
            jsonObject.put("payment", paymentDao.queryGatherPayment(bufferD.toString()));
        }
        return jsonObject;
    }

    @Override
    public JSONObject queryPatient(Map<String, Object> map) throws MessageException, ParseException {
        String comma = ",";
        JSONObject jsonObject = new JSONObject();
        PayserviceBean payserviceBean = new PayserviceBean();
        payserviceBean.setIsuse("1");
        List<PayserviceBean> payserviceBeans = payserviceService.queryPayservice(payserviceBean);
        jsonObject.put("payService", payserviceBeans);
        StringBuffer sql = new StringBuffer();
        StringBuffer bufferTAB = new StringBuffer();
        StringBuffer sumReceivable = new StringBuffer();
        if (payserviceBeans == null || payserviceBeans.isEmpty()) {
            sql.append(" SELECT * FROM (");
            bufferTAB.append("SELECT ")
                    .append(" A.PATIENT_ID patientId, A.HOSP_NUM hospNum, A.NAME name, A.SEX sex,")
                    .append(" A.AGE age, A.HOSP_TIME hospTime, A.IN_HOSP inHosp, A.OUT_HOSP outHosp, A.DEPT_ID deptId,")
                    .append(" NULL as receivable, A.UPDATE_TIME updateTime,C.NAME DEPTNAME,A.PATITYPEID patitypeid,D.PATITYPENAME patitypename,")
                    .append(" A.MITYPEID mitypeid,E.MITYPENAME mitypename,A.ACCOUNT_ID,F.ACCOUNT_NAME")
                    .append(" FROM PATIENT A")
                    .append(" LEFT JOIN DEPARTMENT C ON  A.DEPT_ID = C.DEPT_ID")
                    .append(" LEFT JOIN PATIENTTYPE D")
                    .append(" ON A.PATITYPEID = D.PATITYPEID ")
                    .append(" LEFT JOIN MEDICINSURTYPE E ")
                    .append(" ON A.MITYPEID = E.MITYPEID ")
                    .append(" LEFT JOIN ACCOUNT F")
                    .append(" ON A.ACCOUNT_ID = F.ACCOUNT_ID");
        } else {
            bufferTAB.append("SELECT ")
                    .append(" A.PATIENT_ID patientId, A.HOSP_NUM hospNum, A.NAME name, A.SEX sex,")
                    .append(" A.AGE age, A.HOSP_TIME hospTime, A.IN_HOSP inHosp, A.OUT_HOSP outHosp, A.DEPT_ID deptId,")
                    .append(" A.UPDATE_TIME updateTime,B.*,C.NAME DEPTNAME,A.PATITYPEID patitypeid,D.PATITYPENAME patitypename,")
                    .append(" A.MITYPEID mitypeid,E.MITYPENAME mitypename,A.ACCOUNT_ID,F.ACCOUNT_NAME")
                    .append(" FROM PATIENT A")
                    .append(" LEFT JOIN (")
                    .append(" SELECT E.PATIENT_ID,");
            for (int i = 0; i < payserviceBeans.size(); i++) {
                PayserviceBean bean = payserviceBeans.get(i);
                String receivable = "RECEIVABLE_".concat(bean.getPayserviceId());
                bufferTAB.append(" MAX(CASE E.PAYSERVICE_ID WHEN ").append(bean.getPayserviceId()).append(" THEN E.RECEIVABLE ELSE 0 END) ").append(receivable);
                sumReceivable.append("TAB.").append(receivable);
                if (i != payserviceBeans.size() - 1) {
                    bufferTAB.append(comma);
                    sumReceivable.append("+");
                }
            }
            bufferTAB.append(" FROM ( SELECT PAYMENT.PATIENT_ID,MAX(PAYMENT.ENDTIME) ENDTIME,PAYMENT.PAYSERVICE_ID ")
                    .append(" FROM PAYMENT,( SELECT PATIENT_ID,MAX(PAYMENTTIME) PAYMENTTIME FROM PAYMENT WHERE ISUSE = 1 GROUP BY PATIENT_ID")
                    .append(" ) PAYM WHERE PAYMENT.PATIENT_ID = PAYM.PATIENT_ID AND PAYMENT.PAYMENTTIME = PAYM.PAYMENTTIME")
                    .append(" GROUP BY PAYMENT.PATIENT_ID,PAYMENT.PAYSERVICE_ID ) D, PAYMENT E")
                    .append(" WHERE  D.PATIENT_ID = E.PATIENT_ID AND D.ENDTIME = E.ENDTIME AND D.PAYSERVICE_ID = E.PAYSERVICE_ID GROUP BY E.PATIENT_ID")
                    .append(" ) B ON A.PATIENT_ID = B.PATIENT_ID LEFT JOIN DEPARTMENT C ON  A.DEPT_ID = C.DEPT_ID ")
                    .append(" LEFT JOIN PATIENTTYPE D")
                    .append(" ON A.PATITYPEID = D.PATITYPEID ")
                    .append(" LEFT JOIN MEDICINSURTYPE E ")
                    .append(" ON A.MITYPEID = E.MITYPEID ")
                    .append(" LEFT JOIN ACCOUNT F")
                    .append(" ON A.ACCOUNT_ID = F.ACCOUNT_ID");
            //System.out.println(bufferTAB.toString());
            sql.append(" SELECT TAB.*, ").append(sumReceivable).append(" AS receivable").append(" FROM (");
        }
        if (map != null) {
            bufferTAB.append(" WHERE 1 = 1");
            //患者姓名
            if (map.get("name") != null && StringUtils.isNotBlank(String.valueOf(map.get("name")))) {
                bufferTAB.append("AND A.NAME LIKE '%").append(String.valueOf(map.get("name"))).append("%'");
            }
            //科室
            if (map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
                bufferTAB.append(" AND A.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
            }
            //住院号
            if (map.get("hospNum") != null && StringUtils.isNotBlank(String.valueOf(map.get("hospNum")))) {
                bufferTAB.append(" AND A.HOSP_NUM LIKE '%").append(String.valueOf(map.get("hospNum"))).append("%'");
            }
            //性别
            if (map.get("sex") != null && StringUtils.isNotBlank(String.valueOf(map.get("sex")))) {
                String sex = String.valueOf(map.get("sex"));
                if ("1".equals(sex) || "男".equals(sex)) {
                    //男
                    bufferTAB.append(" AND A.SEX = '1'");
                } else if ("2".equals(sex) || "女".equals(sex)) {
                    //女
                    bufferTAB.append(" AND A.SEX = '2'");
                }
            }
            //入院日期
            if (map.get("hospTime") != null && StringUtils.isNotBlank(String.valueOf(map.get("hospTime")))) {
                String hospTime = TimeUtil.getDateYYYY_MM_DD(TimeUtil.parseAnyDate(String.valueOf(map.get("hospTime"))));
                bufferTAB.append(" AND CONVERT(VARCHAR(100), A.HOSP_TIME, 23) = '").append(hospTime).append("'");
            }
            //出院日期
            if (map.get("outHosp") != null && StringUtils.isNotBlank(String.valueOf(map.get("outHosp")))) {
                String outHosp = TimeUtil.getDateYYYY_MM_DD(TimeUtil.parseAnyDate(String.valueOf(map.get("outHosp"))));
                bufferTAB.append(" AND CONVERT(VARCHAR(100), A.OUT_HOSP, 23) = '").append(outHosp).append("'");
            }
            //是否在院
            if (map.get("inHosp") != null && StringUtils.isNotBlank(String.valueOf(map.get("inHosp")))) {
                String inHosp = String.valueOf(map.get("inHosp"));
                if ("1".equals(inHosp)) {
                    bufferTAB.append(" AND A.IN_HOSP = '1'");
                } else if ("0".equals(inHosp)) {
                    bufferTAB.append(" AND A.IN_HOSP = '0'");
                }
            }
            //患者类型
            if (map.get("patitypeid") != null && StringUtils.isNotBlank(String.valueOf(map.get("patitypeid")))) {
                bufferTAB.append(" AND A.PATITYPEID = '").append(String.valueOf(map.get("patitypeid"))).append("'");
            }
            //医保类型
            if (map.get("mitypeid") != null && StringUtils.isNotBlank(String.valueOf(map.get("mitypeid")))) {
                bufferTAB.append(" AND A.MITYPEID = '").append(String.valueOf(map.get("mitypeid"))).append("'");
            }
        }
        sql.append(bufferTAB).append(" ) TAB");
        jsonObject.put("payment", paymentDao.queryGatherPayment(sql.toString()));
        return jsonObject;
    }

    @Override
    public List<PaymentBean> queryPaymentByPatientId(String patientId) {
        return paymentDao.queryPaymentByPatientId(patientId);
    }

    @Override
    public List<PaymentBean> queryPaymentByHospNum( String hospNum,String patientId) {
        return paymentDao.queryPaymentByHospNum(hospNum,patientId);
    }

    @Override
    public List<PaymentBean> queryPaymentByPatientIdTime(Map<String, Object> map) {
        return paymentDao.queryPaymentByPatientIdTime(map);
    }

    @Override
    public void addPayment(List<PaymentBean> list) throws MessageException {
        paymentDao.addPayment(list);
    }

    @Override
    public void updatePayment(List<PaymentBean> list) throws MessageException {
        paymentDao.updatePayment(list);
    }


    @Override
    public void deletePayment(List<PaymentBean> list) throws MessageException {
        paymentDao.deletePayment(list);
    }

    @Override
    public PaymentBean queryPaymentByPatientIdPayserviceId(PaymentBean paymentBean) throws MessageException {
        if (paymentBean == null || StringUtils.isBlank(paymentBean.getPatientId()) || StringUtils.isBlank(paymentBean.getPayserviceId()))
            throw new MessageException("患者ID或收费项目ID为空!");
        QueryWrapper<PaymentBean> wrapper = new QueryWrapper<>();
        wrapper.select("*");
        wrapper.eq(true, "PATIENT_ID", paymentBean.getPatientId());
        wrapper.eq(true, "PAYSERVICE_ID", paymentBean.getPayserviceId());
        wrapper.eq(true, "ISUSE", 1);
        wrapper.orderByDesc("PAYMENTTIME");
        List<PaymentBean> list = paymentDao.selectList(wrapper);
        if (list.size() > 0) return list.get(0);
        return null;
    }

    @Override
    public Map<String, Object> queryGatherPaymentListInfo(PatientBean patientBean) throws Exception {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("paymentList", paymentDao.queryGatherPaymentList(patientBean));
        dataMap.put("paymentTotal", paymentDao.queryGatherPaymentTotal(patientBean));
        return dataMap;
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