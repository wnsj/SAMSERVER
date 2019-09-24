package com.jiubo.sam.service;

import com.jiubo.sam.bean.MedicinsurtypeBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.exception.MessageException;

import java.util.List;

/**
 * <p>
 *  医保类型服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
public interface MedicinsurtypeService extends IService<MedicinsurtypeBean> {

    //查询医保类型
    public List<MedicinsurtypeBean> queryMedicinsurtype(MedicinsurtypeBean medicinsurtypeBean);

    //添加医保类型
    public void addMedicinsurtype(MedicinsurtypeBean medicinsurtypeBean) throws MessageException;

    //修改医保类型
    public void updateMedicinsurtype(MedicinsurtypeBean medicinsurtypeBean) throws MessageException;
}
