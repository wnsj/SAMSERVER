package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PatinetMarginBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
}
