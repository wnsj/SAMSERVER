package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.MedicinsurtypeBean;
import com.jiubo.sam.dao.MedicinsurtypeDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.MedicinsurtypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 医保类型服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
@Service
public class MedicinsurtypeServiceImpl extends ServiceImpl<MedicinsurtypeDao, MedicinsurtypeBean> implements MedicinsurtypeService {

    @Autowired
    private MedicinsurtypeDao medicinsurtypeDao;

    @Override
    public List<MedicinsurtypeBean> queryMedicinsurtype(MedicinsurtypeBean medicinsurtypeBean) {
        return medicinsurtypeDao.queryMedicinsurtype(medicinsurtypeBean);
    }

    @Override
    public void addMedicinsurtype(MedicinsurtypeBean medicinsurtypeBean) throws MessageException {
        if (StringUtils.isBlank(medicinsurtypeBean.getMitypename())) throw new MessageException("医保类型名不能为空!");
        QueryWrapper<MedicinsurtypeBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "MITYPENAME", medicinsurtypeBean.getMitypename());
        if (medicinsurtypeDao.selectList(queryWrapper).size() > 0) throw new MessageException("医保类型名不能重复!");
        if (medicinsurtypeDao.insert(medicinsurtypeBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    public void updateMedicinsurtype(MedicinsurtypeBean medicinsurtypeBean) throws MessageException {
        QueryWrapper<MedicinsurtypeBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "MITYPENAME", medicinsurtypeBean.getMitypename());
        queryWrapper.ne("MITYPEID", medicinsurtypeBean.getMitypeid());
        if (medicinsurtypeDao.selectList(queryWrapper).size() > 0) throw new MessageException("医保类型名不能重复!");
        if (medicinsurtypeDao.updateById(medicinsurtypeBean) <= 0) throw new MessageException("操作失败!");
    }
}
