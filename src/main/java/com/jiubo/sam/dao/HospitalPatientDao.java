package com.jiubo.sam.dao;

import com.jiubo.sam.bean.HospitalPatientBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.request.HospitalPatientCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 住院门诊 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
public interface HospitalPatientDao extends BaseMapper<HospitalPatientBean> {

    List<HospitalPatientBean> selectByCondition(HospitalPatientCondition hospitalPatientBean);

    PaymentDetailsBean getNewestBalance(@Param("hospNum") String hospNum);
}
