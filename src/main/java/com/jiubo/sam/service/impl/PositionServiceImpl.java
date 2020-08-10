package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.PositionBean;
import com.jiubo.sam.dao.PositionDao;
import com.jiubo.sam.service.PositionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author syl
 * @since 2020-08-07
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionDao, PositionBean> implements PositionService {

    @Autowired
    private PositionDao positionDao;

    /**
     * 岗位列表
     * @param positionBean
     * @return
     */
    @Override
    public Page<PositionBean> getPosByCondition(PositionBean positionBean) {
        Page<PositionBean> page = new Page<>();
        page.setCurrent(positionBean.getCurrent());
        page.setSize(positionBean.getPageSize());
        return page.setRecords(positionDao.getPosByCondition(page,positionBean));
    }

    /**
     * 添加岗位
     * @param positionBean
     */
    @Override
    public void addPos(PositionBean positionBean) {
        positionBean.setCreateDate(new Date());
        positionDao.insert(positionBean);
    }

    /**
     * 更新岗位
     * @param positionBean
     */
    @Override
    public void patchPos(PositionBean positionBean) {
        positionDao.updateById(positionBean);
    }

    @Override
    public List<PositionBean> getAllPos(PositionBean positionBean) {
        return positionDao.getPosByCondition(positionBean);
    }


}
