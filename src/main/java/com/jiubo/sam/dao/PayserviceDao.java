package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PayserviceBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 收费项目 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PayserviceDao extends BaseMapper<PayserviceBean> {

    //查询收费项目
    public List<PayserviceBean> queryPayservice(PayserviceBean payserviceBean);
}
