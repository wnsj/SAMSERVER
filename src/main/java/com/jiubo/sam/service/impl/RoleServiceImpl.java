package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.RoleBean;
import com.jiubo.sam.dao.RoleDao;
import com.jiubo.sam.service.RoleService;
import com.jiubo.sam.util.CollectionsUtils;
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
 * @author dx
 * @since 2020-05-20
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, RoleBean> implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<RoleBean> getAllRole() {
        return roleDao.getRoleByIdList(new RoleBean());
    }

    @Override
    public Page<RoleBean> getRoleByCondition(RoleBean roleBean) {
        Page<RoleBean> page = new Page<>();
        page.setCurrent(StringUtils.isBlank(roleBean.getCurrent()) ? 1L : Long.parseLong(roleBean.getCurrent()));
        page.setSize(StringUtils.isBlank(roleBean.getPageSize()) ? 10L : Long.parseLong(roleBean.getPageSize()));
        List<RoleBean> roleByIdList = roleDao.getRoleByIdList(page, roleBean);
        if (!CollectionsUtils.isEmpty(roleByIdList)) {
            for (RoleBean roleBean1 : roleByIdList) {
                if (roleBean1.getState() == 1) {
                    roleBean1.setStateLabel("启用");
                } else {
                    roleBean1.setStateLabel("禁用");
                }

            }
        }
        return page.setRecords(roleByIdList);
    }

    @Override
    public void addRole(RoleBean roleBean) {
        roleBean.setCreateTime(new Date());
        roleDao.addRole(roleBean);
    }

    @Override
    public void patchRoleById(RoleBean roleBean) {
            roleDao.patchRoleById(roleBean);
    }

}
