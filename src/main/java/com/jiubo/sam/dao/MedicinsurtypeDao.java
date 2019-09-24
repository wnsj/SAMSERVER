package com.jiubo.sam.dao;

import com.jiubo.sam.bean.MedicinsurtypeBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 医保类型 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
public interface MedicinsurtypeDao extends BaseMapper<MedicinsurtypeBean> {

    //查询医保类型
    public List<MedicinsurtypeBean> queryMedicinsurtype(MedicinsurtypeBean medicinsurtypeBean);

}
