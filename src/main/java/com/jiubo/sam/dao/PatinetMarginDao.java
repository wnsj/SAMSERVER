package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PatinetMarginBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
public interface PatinetMarginDao extends BaseMapper<PatinetMarginBean> {

    List<PatinetMarginBean> selecAllList(String hospNum);

    List<PatinetMarginBean> getMByIdCard(@Param("idCard") String idCard);

    int rollbackMargin(@Param("idCard") String idCard);

    int deletePdByWaterNum(@Param("waterNum") String waterNum);

    int deleteHpByWaterNum(@Param("waterNum") String waterNum);
}
