package com.jiubo.sam.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.NoMedicalBean;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.dto.OpenServiceReceive;
import com.jiubo.sam.dto.PayServiceDto;
import com.jiubo.sam.dto.PdCondition;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mwl
 * @since 2020-08-10
 */
public interface PaPayserviceDao extends BaseMapper<PaPayserviceBean> {

    //查询需要缴费的项目
    List<PaPayserviceBean> queryPaPayService(PaPayserviceBean paPayserviceBean);

    //添加患者缴费项目type=0
    int addPaPayService(PaPayserviceBean paPayserviceBean);

    //添加缴费项目type=1
    int addPaPayServiceByType(PaPayserviceBean paPayserviceBean);

    //添加需要缴费的项目list
    int addAndUpdatePaPayService(List<PaPayserviceBean> list);

    //修改患者缴费项目type=0
    int updatePaPayService(PaPayserviceBean paPayserviceBean);

    //修改患者缴费项目type=1
    int updatePaPayServiceByType(PaPayserviceBean paPayserviceBean);

    //根据出院-停止所有缴费的项目
    int updatePaPayServiceByPatient(String hospNum, LocalDateTime now);

    //  停止的项目开启
    int updatePaPayServiceById(PaPayserviceBean paPayserviceBean);

    /**
     * 根据患者id 项目id 查询历史
     * @param paPayserviceBean
     * @return
     */
    List<PaPayserviceBean> getPaPayServiceByCon(@Param("paPayserviceBean") PaPayserviceBean paPayserviceBean);
    List<PaPayserviceBean> getPaPayServiceByCon(Page page, @Param("paPayserviceBean") PaPayserviceBean paPayserviceBean);

    List<PaPayserviceBean>  selectPaPayService(String hospNum);

    List<PaPayserviceBean> getSectionDateCover(OpenServiceReceive openServiceReceive);

    List<PaPayserviceBean> getDefaultDateCover(OpenServiceReceive openServiceReceive);

    int addUserService(PayServiceDto payServiceDto);

    List<PaPayserviceBean> selectByHospNum(String hospNum,String endDate,String payserviceId,Integer id);

    List<PaPayserviceBean> selectByHospNumAndPayServiceId(String hospNum, String payserviceId,Integer id);

    List<PaPayserviceBean> selectByHospNumAndOutHosp(String hospNum, String outHosp);

    List<PaPayserviceBean> selectOpenByHospNumAndOutHosp(String hospNum, String outHosp);

    List<PaPayserviceBean> selectByHospNumAndOutHosps(String hospNum, String outHosp);

    List<NoMedicalBean> getPBByCondition(PdCondition pdCondition);

    List<String> getDateTable(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    List<PaPayserviceBean> selectByHospNumStart(String hospNum, String begDate, String payserviceId);


    Integer selectType(Long id);

    Integer selectOpen(String idCard1);

    PayserviceBean selectIsUse(int i);

}
