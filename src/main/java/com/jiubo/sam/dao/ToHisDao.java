package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.dto.PatientHiSDto;
import com.jiubo.sam.dto.PatientMoneyCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 患者基础信息 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface ToHisDao extends BaseMapper<PatientBean> {

    //患者精确查询
    List<PatientBean> accurateQuery(@Param("idCard") String idCard);

    //添加患者信息
    int addHisPatient(PatientHiSDto patientHiSDto);

    List<PaPayserviceBean> getOpenDefault();

    List<PaymentBean> getNewestByPAndS();

    int addPPReset(List<PaPayserviceBean> list);

    int patchPPReset(List<PaPayserviceBean> list);

    void patchPPById(PaPayserviceBean paPayserviceBean);

    void addPP(PaPayserviceBean paPayserviceBean);

    List<PaPayserviceBean> getSection();

    int deletePP(@Param("id") String id);

    int patchPPList(List<PaPayserviceBean> list);

    void patchPP(PaPayserviceBean paPayserviceBean);
}
