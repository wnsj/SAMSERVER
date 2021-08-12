package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.dto.NoMeDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交费 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PaymentDao extends BaseMapper<PaymentBean> {

    //添加交费信息
    //public int addPayment(PaymentBean paymentBean);

    //收费信息汇总查询
    public List<Map<String, Object>> queryGatherPayment(@Param("sql") String sql);

    //根据患者id查询交费信息
    public List<PaymentBean> queryNewByPatientIdPayment(PaymentBean paymentBean);

    //根据患者id查询交费信息
    public List<PaymentBean> queryPaymentByPatientId(String patientId);

    //根据患者id查询交费信息
    public List<PaymentBean> queryPaymentByHospNum(@Param("hospNum") String hospNum, @Param("patientId") String patientId);

    //据患者Id及收费时间查询患者信息及缴费信息
    public List<PaymentBean> queryPaymentByPatientIdTime(Map<String, Object> map);

    //添加交费信息
    public int addPayment(List<PaymentBean> list);

    //修改交费信息
    public int updatePayment(List<PaymentBean> list);

    //删除缴费信息
    public void deletePayment(List<PaymentBean> list);

    //缴费统计
    @Deprecated
    public List<Map<String, Object>> queryGatherPaymentList(PatientBean patientBean);

    //缴费统计汇总
    @Deprecated
    public Map<String, Object> queryGatherPaymentTotal(PatientBean patientBean);

    // 查询患者缴费详情(旧)
    List<PaymentBean> getPaymentDetails(PaymentBean paymentBean);

    // 查询患者缴费详情(新)
    List<PaymentBean> getPd(PaymentBean paymentBean);

    // 非医疗费报表
    List<NoMeDto> getPayNoMe(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    //缴费统计
    @Deprecated
    public List<Map<String, Object>> queryGatherPaymentTwo(PatientBean patientBean);

    //缴费统计汇总
    @Deprecated
    public Map<String, Object> queryGatherPaymentTotalTwo(PatientBean patientBean);

    //缴费统计
    public IPage<Map> queryGatherPaymentTh(@Param("page") IPage page, @Param("patientBean") PatientBean patientBean);

    //缴费统计汇总
    public Map<String, Object> queryGatherPaymentTotalTh(@Param("patientBean") PatientBean patientBean);

    List<PayTotalDto> getPayTotal(@Param("patientBean") PatientBean patientBean);
    List<PayTotalDto> getPayTotalPage(List<String> numList);
    //缴费统计详情
    List<Map<String, Object>> queryPatientGatherDetails(PaymentBean paymentBean);

    //缴费统计合并
    public IPage<Map> queryGatherPaymentMergeTh(@Param("page") IPage page, @Param("patientBean") PatientBean patientBean);

    List<PaymentOne> getPaymentOne(PaymentOneCondition condition);
}
