package com.jiubo.sam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.*;
import com.jiubo.sam.dao.MedicalExpensesDao;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.dao.PaymentDao;
import com.jiubo.sam.dao.PayserviceDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.PatientService;
import com.jiubo.sam.service.PaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.service.PayserviceService;
import com.jiubo.sam.util.CollectionsUtils;
import com.jiubo.sam.util.TimeUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.jiubo.sam.common.PayDetailsConstant.*;

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

    @Autowired
    private MedicalExpensesDao medicalExpensesDao;

    @Autowired
    private PayserviceDao payserviceDao;

    @Autowired
    private LogRecordsService logRecordsService;

    @Autowired
    private PatientDao patientDao;

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
        String paymentStatus = "";
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
            bufferD.append("SELECT DISTINCT EMP.emp_name,D.*,C.*,PPP.*,DATEDIFF(day, PPP.ENDDATE, GETDATE()) AS DAY_NUM,E.NAME DEPTNAME,F.PATITYPENAME,G.MITYPENAME,H.NAME ACCNAME FROM (");
            bufferA.append("SELECT A.PATIENT_ID,B.EMP_ID,B.DEPT_ID,A.PAYMENTTIME,B.payment_status,MAX(A.PRICE) PRICE,MAX(A.DAYS) DAYS,A.ACCOUNT_ID,");
            // 孙云龙修改 （由于需要记录历史记录 所以 查的是缴费表里的科室id 而不是【原逻辑】=>患者表里的）修改处 查询 添加 TAB.DEPT_ID
            bufferTAB.append("SELECT TAB.EMP_ID,TAB.PATIENT_ID,TAB.DEPT_ID,TAB.PAYMENTTIME,TAB.payment_status,MAX(TAB.PRICE) PRICE,MAX(TAB.DAYS) DAYS,TAB.ACCOUNT_ID ACCID,");
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
            //【二次修改 2020-08-14 从患者表多查出个EMP_ID 并将其作为查询条件】
            bufferA.append(" FROM PAYMENT A,( SELECT EMP_ID,PATIENT_ID,DEPT_ID,PAYMENTTIME,payment_status FROM PAYMENT ");
            // end
            if (map != null && map.get("begDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("begDate")))
                    && map.get("endDate") != null && StringUtils.isNotBlank(String.valueOf(map.get("endDate")))) {
                bufferA.append(" WHERE ");
                bufferA.append(" PAYMENTTIME >= '").append(String.valueOf(map.get("begDate"))).append("'");
                String endDate = String.valueOf(map.get("endDate"));
                endDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.dateAdd(TimeUtil.parseAnyDate(endDate), TimeUtil.UNIT_DAY, 1));
                bufferA.append(" AND PAYMENTTIME < '").append(endDate).append("'");
            }
            bufferA.append(" GROUP BY PATIENT_ID,EMP_ID,DEPT_ID,PAYMENTTIME,payment_status ) B");
            bufferA.append(" WHERE A.PATIENT_ID = B.PATIENT_ID AND A.PAYMENTTIME = B.PAYMENTTIME");
            // 孙云龙修改 查询条件deptId 改为 缴费表里的deptId
            // 科室条件查询

//            if (StringUtils.isNotBlank(String.valueOf(map.get("deptId"))) && StringUtils.isNotBlank(String.valueOf(map.get("hospNum")))){
//                bufferA.append(" AND B.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
//            }else {
//            bufferA.append(" AND B.DEPT_ID = '").append(String.valueOf(map.get("deptId"))).append("'");
                if(jurdgePatientInDept((String) map.get("hospNum"),(List<String>) map.get("deptList"))==true){
                    List<String> deptList = (List<String>) map.get("deptList");
                    if (deptList != null && !deptList.isEmpty()) {
                        String deptStr = "";
                        int size = deptList.size();
                        for (int i = 0; i < size; i++) {
                            String str = String.valueOf(deptList.get(i));
                            deptStr = i == size - 1 ? deptStr.concat(str) : deptStr.concat(str).concat(comma);
                        }
                        bufferA.append(" AND B.DEPT_ID IN (").append(deptStr).append(") ");
                    }
                }
//            }

//            if (null != map && map.get("deptId") != null && StringUtils.isNotBlank(String.valueOf(map.get("deptId")))) {
//
//            }else {
//

//            }


            // 由于多查了字段 科室id 所以分组条件中需添加 B.DEPT_ID
            bufferA.append(" GROUP BY A.PATIENT_ID,B.DEPT_ID,B.EMP_ID,A.PAYSERVICE_ID,A.PAYMENTTIME,A.ACCOUNT_ID,B.payment_status ");
            // end
            bufferTAB.append(bufferActualpayment);
            bufferTAB.append(" ACTUALPAYMENT FROM (");
            bufferTAB.append(bufferA);
            // 孙云龙修改 由于多查了字段 科室id 所以分组条件中需添加 TAB.DEPT_ID
            bufferTAB.append(" ) TAB GROUP BY TAB.EMP_ID,TAB.PATIENT_ID,TAB.DEPT_ID,TAB.PAYMENTTIME,TAB.ACCOUNT_ID,TAB.payment_status ");
            // end
            bufferD.append(bufferTAB);
            //bufferD.append(" ) D,PATIENT C,DEPARTMENT E,PATIENTTYPE F,MEDICINSURTYPE G");
            // 孙云龙修改 在此处 D表 关联 科室表 查出 科室名字（因为科室查询条件已经在D表中）
            bufferD.append(" ) D ");
            bufferD.append(" LEFT JOIN employee EMP ON D.EMP_ID = EMP.id");
            bufferD.append(" LEFT JOIN sys_account H ON H.SA_ID = D.ACCID");
            bufferD.append(" LEFT JOIN DEPARTMENT E ON D.DEPT_ID = E.DEPT_ID,PATIENT C");
//            bufferD.append(" LEFT JOIN DEPARTMENT E ON  C.DEPT_ID = E.DEPT_ID");
            // end
            bufferD.append(" LEFT JOIN PATIENTTYPE F ON C.PATITYPEID = F.PATITYPEID");
            bufferD.append(" LEFT JOIN MEDICINSURTYPE G ON C.MITYPEID = G.MITYPEID");
            //添加结束时间
            bufferD.append(" LEFT JOIN (SELECT PP.PATIENT_ID,MAX(PP.ENDTIME) FROM");
            bufferD.append(" (SELECT P.PATIENT_ID,P.ENDTIME,P.payment_status FROM PAYMENT P WHERE ENDTIME IS NOT NULL " ) ;
            if (map != null && map.get("payment_status") != null && StringUtils.isNotBlank(String.valueOf(map.get("payment_status")))) {
                paymentStatus = String.valueOf(map.get("payment_status")) ;
                bufferD.append(" AND P.payment_status =").append(paymentStatus);
            }
            bufferD.append( " GROUP BY P.PATIENT_ID,P.ENDTIME,p.payment_status )PP GROUP BY PP.PATIENT_ID ) AS PPP (PATIENT_ID,ENDDATE) ON PPP.PATIENT_ID=C.PATIENT_ID");
            //bufferD.append(" WHERE D.PATIENT_ID = C.PATIENT_ID AND C.DEPT_ID = E.DEPT_ID AND C.PATITYPEID=F.PATITYPEID AND C.MITYPEID=G.MITYPEID");
            bufferD.append(" WHERE D.PATIENT_ID = C.PATIENT_ID ");
            if (paymentStatus != null && paymentStatus != ""){
                bufferD.append(" and D.payment_status = ").append(paymentStatus);
            }
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
                if (map.get("inHosp") != null && StringUtils.isNotBlank(String.valueOf(map.get("inHosp")))) {
                    bufferD.append(" AND C.IN_HOSP = '").append(String.valueOf(map.get("inHosp"))).append("'");
                }
                // 维护医生
                if (map.get("empId") != null && StringUtils.isNotBlank(String.valueOf(map.get("empId")))) {
                    bufferD.append(" AND EMP.id = '").append(map.get("empId")).append("'");
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
                    .append(" ON A.ACCOUNT_ID = F.ACCOUNT_ID ");

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

    //判断患者是否在这个部门  住院号不在这个几个有权限的部门下或者为空 使用部门条件返回true，否则返回false
    public boolean jurdgePatientInDept(String hospNum,List<String> deptList) throws Exception{
        List<PatientBean> pbList = patientDao.queryPatientInfo(new PatientBean().setHospNum(hospNum));
        if (pbList.size()>0){
            String deptId = pbList.get(0).getDeptId();
            if(null != hospNum && StringUtils.isNotBlank(String.valueOf(hospNum))){
                if (deptList != null && !deptList.isEmpty()) {
                    String deptStr = "";
                    int size = deptList.size();
                    for (int i = 0; i < size; i++) {
                        String str = String.valueOf(deptList.get(i));
                        if (deptId.equals(str)){
                            return false;
                        }
                    }
                }
                return true;  //有权限的部门列表里没有这个住院号，使用
            }else {
                return true;   //住院号为空，使用
            }
        }else {
            return false;    //没查到患者，不使用
        }
    }

    @Override
    public List<PaymentBean> queryPaymentByPatientId(String patientId) {
        return paymentDao.queryPaymentByPatientId(patientId);
    }

    @Override
    public List<PaymentBean> queryPaymentByHospNum(String hospNum, String patientId) {
        return paymentDao.queryPaymentByHospNum(hospNum, patientId);
    }

    @Override
    public List<PaymentBean> queryPaymentByPatientIdTime(Map<String, Object> map) {
        return paymentDao.queryPaymentByPatientIdTime(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPayment(List<PaymentBean> list) throws Exception {
        System.out.println(list);
        paymentDao.addPayment(list);

        if(StringUtils.isBlank(list.get(0).getAccountId())) throw new MessageException("系统账号有问题请重新登录");

        if (list.size()>0){
            //添加日志
            logRecordsService.insertLogRecords(new LogRecordsBean()
                    .setHospNum(list.get(0).getHospNum())
                    .setOperateId(Integer.valueOf(list.get(0).getAccountId()))
                    .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                    .setOperateModule("非医疗费缴费")
                    .setOperateType("添加")
                    .setLrComment(list.toString())
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePayment(List<PaymentBean> list) throws Exception {
        paymentDao.updatePayment(list);

        if (list.size()>0){
            String hospNum = patientDao.queryPatientInfo(new PatientBean().setPatientId(list.get(0).getPatientId())).get(0).getHospNum();
            if(StringUtils.isBlank(list.get(0).getAccountId())) throw new MessageException("系统账号有问题请重新登录");
            //添加日志
            logRecordsService.insertLogRecords(new LogRecordsBean()
                    .setHospNum(hospNum)
                    .setOperateId(Integer.valueOf(list.get(0).getAccountId()))
                    .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                    .setOperateModule("非医疗费缴费")
                    .setOperateType("修改")
                    .setLrComment(list.toString())
            );
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePayment(List<PaymentBean> list) throws Exception {
        paymentDao.deletePayment(list);

        if (list.size()>0){
            String hospNum = patientDao.queryPatientInfo(new PatientBean().setPatientId(list.get(0).getPatientId())).get(0).getHospNum();

            if(StringUtils.isBlank(list.get(0).getAccountId())) throw new MessageException("系统账号有问题请重新登录");

            //添加日志
            logRecordsService.insertLogRecords(new LogRecordsBean()
                    .setHospNum(hospNum)
                    .setOperateId(Integer.valueOf(list.get(0).getAccountId()))
                    .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                    .setOperateModule("非医疗费缴费")
                    .setOperateType("删除")
                    .setLrComment(list.toString())
            );
        }
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
        //dataMap.put("paymentList", paymentDao.queryGatherPaymentList(patientBean));
        //dataMap.put("paymentTotal", paymentDao.queryGatherPaymentTotal(patientBean));
        //List<Map<String, Object>> maps = paymentDao.queryGatherPaymentTwo(patientBean);
        Page<Object> page = new Page<>();
        page.setOptimizeCountSql(false);
        page.setCurrent(Long.valueOf(StringUtils.isBlank(patientBean.getPage()) ? "0" : patientBean.getPage()));
        page.setSize(Long.valueOf(StringUtils.isBlank(patientBean.getPageSize()) ? "10" : patientBean.getPageSize()));
        page.addOrder(new OrderItem().setAsc(true).setColumn("PATIENT_ID"));
        IPage iPage = null;


        if ("1".equals(patientBean.getIsMerge())) {
            iPage = paymentDao.queryGatherPaymentMergeTh(page, patientBean);
        } else {
            iPage = paymentDao.queryGatherPaymentTh(page, patientBean);
            log.error("haha");
        }


//        List<Map<String, Object>> maps = iPage.getRecords();
//        if ("1".equals(patientBean.getIsMerge())) {
//            Map<String, List<Map<String, Object>>> patientListMap = maps.stream().collect(Collectors.groupingBy(item -> String.valueOf(item.get("PATIENT_ID"))));
//            maps = new ArrayList<>();
//            for (Map.Entry<String, List<Map<String, Object>>> entry : patientListMap.entrySet()) {
//                List<Map<String, Object>> value = entry.getValue();
//                Map<String, Object> map = null;
//                for (int i = 0; i < value.size(); i++) {
//                    Map samll = value.get(i);
//                    if (i == 0) {
//                        map = samll;
//                    } else {
//                        map.put("PAYSERVICENAME", String.valueOf(map.get("PAYSERVICENAME")).concat(",").concat(String.valueOf(samll.get("PAYSERVICENAME"))));
//                        map.put("TOTAL", new BigDecimal(String.valueOf(map.get("TOTAL") == null ? 0 : map.get("TOTAL"))).add(new BigDecimal(String.valueOf(samll.get("TOTAL") == null ? 0 : samll.get("TOTAL")))).toString());
//                        int days = Integer.parseInt(String.valueOf(map.get("DAYS")));
//                        int day = Integer.parseInt(String.valueOf(samll.get("DAYS")));
//                        map.put("DAYS", days < day ? day : days);
//                        Date endtime = (Date) map.get("ENDTIME");
//                        Date samllEndTime = (Date) samll.get("ENDTIME");
//                        if (endtime != null && samllEndTime != null)
//                            map.put("ENDTIME", endtime.getTime() > samllEndTime.getTime() ? samllEndTime : endtime);
//                    }
//                }
//                if (map != null) maps.add(map);
//            }
//            iPage.setCurrent(0);
//            iPage.setPages(1);
//            iPage.setTotal(maps.size());
//            iPage.setRecords(maps);
//        }
        dataMap.put("paymentData", iPage);
        dataMap.put("paymentTotal", paymentDao.queryGatherPaymentTotalTh(patientBean));
        return dataMap;
    }

    @Override
    public PayCount getPaymentDetails(PaymentBean paymentBean) {
        if (!StringUtils.isBlank(paymentBean.getPatientId())) {
            PayCount payCount = new PayCount();
            List<PaymentBean> paymentBeanList = paymentDao.getPaymentDetails(paymentBean);
            if (StringUtils.isBlank(paymentBean.getPayserviceId()) || MEDICAL_FEE.equals(paymentBean.getPayserviceId())) {
                List<PaymentBean> medicalDetailsList = medicalExpensesDao.getMedicalDetails(paymentBean);
                if (!CollectionsUtils.isEmpty(medicalDetailsList)) {
                    medicalDetailsList = medicalDetailsList.stream().map(item -> item.setPayserviceId(MEDICAL_FEE)).collect(Collectors.toList());
                    paymentBeanList.addAll(medicalDetailsList);
                }
            }

            if (!CollectionsUtils.isEmpty(paymentBeanList)) {
                // 将 医疗费 非医疗费 按照 时间 (年月日) 分组
                Map<String, List<PaymentBean>> map = paymentBeanList.stream().collect(Collectors.groupingBy(PaymentBean::getPaymenttime));
                // 结果中的数组
                List<PayDetailsBean> payDetailsBeanList = new ArrayList<>();
                // 所有费用汇总
                BigDecimal totalCount = BigDecimal.valueOf(0);
                // 查询所有项目
                List<PayserviceBean> beans = payserviceDao.queryPayservice(new PayserviceBean());
                PayserviceBean payserviceBean = new PayserviceBean();
                payserviceBean.setName(MEDICAL_FEE_LABEL);
                payserviceBean.setPayserviceId(MEDICAL_FEE);
                beans.add(payserviceBean);

                Map<String, List<PayserviceBean>> payMap = null;
                if (!CollectionsUtils.isEmpty(beans)) {
                    payMap = beans.stream().collect(Collectors.groupingBy(PayserviceBean::getPayserviceId));
                }
                for (String payTime : map.keySet()) {
                    PayDetailsBean payDetailsBean = new PayDetailsBean();
                    // 当天费用汇总
                    BigDecimal total = BigDecimal.valueOf(0);
                    // 获取当天数据
                    List<PaymentBean> beanList = map.get(payTime);
                    // 根据项目 分组
                    Map<String, List<PaymentBean>> deptMap = beanList.stream().collect(Collectors.groupingBy(PaymentBean::getPayserviceId));
                    List<PayProjectBean> payProjectBeans = new ArrayList<>();
                    if (null != payMap) {

                        for (String payService : payMap.keySet()) {
                            PayProjectBean projectBean = new PayProjectBean();
                            PayserviceBean bean = payMap.get(payService).get(0);
                            // 获取当天 单项项目收费数据
                            List<PaymentBean> paymentBeans = deptMap.get(payService);
                            BigDecimal ac = BigDecimal.valueOf(0);
                            if (!CollectionsUtils.isEmpty(paymentBeans)) {
                                ac = BigDecimal.valueOf(paymentBeans.get(0).getActualpayment());
                            }
                            projectBean.setProId(bean.getPayserviceId());
                            projectBean.setProName(bean.getName());
                            projectBean.setCharge(ac);
                            total = total.add(ac);

                            payProjectBeans.add(projectBean);
                        }
                    }

                    totalCount = totalCount.add(total);
                    payDetailsBean.setPayTotal(total);
                    payDetailsBean.setPayTime(payTime);
                    payDetailsBean.setProjectBeanList(payProjectBeans);
                    payDetailsBeanList.add(payDetailsBean);
                }
                payCount.setTotalCount(totalCount);
                if (!CollectionsUtils.isEmpty(payDetailsBeanList))
                    payDetailsBeanList = payDetailsBeanList.stream().sorted(Comparator.comparing(PayDetailsBean::getPayTime).reversed()).collect(Collectors.toList());
                payCount.setPayDetailsBeanList(payDetailsBeanList);
                return payCount;
            }
            return payCount;
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> queryPatientGatherDetails(PaymentBean paymentBean) throws Exception {
        return paymentDao.queryPatientGatherDetails(paymentBean);
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