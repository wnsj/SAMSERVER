package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.RoleMenuRefBean;
import com.jiubo.sam.dao.RoleMenuRefDao;
import com.jiubo.sam.service.RoleMenuRefService;
import com.jiubo.sam.util.CollectionsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dx
 * @since 2020-05-20
 */
@Slf4j
@Service
public class RoleMenuRefServiceImpl extends ServiceImpl<RoleMenuRefDao, RoleMenuRefBean> implements RoleMenuRefService {
    @Autowired
    private RoleMenuRefDao roleMenuRefDao;

    @Override
    public void addRMRef(RoleMenuRefBean roleMenuRefBean) {
        bindRMRef(roleMenuRefBean);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void patchRMRef(RoleMenuRefBean roleMenuRefBean) {
        // 解绑
        roleMenuRefDao.deleteRMRByRoleId(roleMenuRefBean.getRoleId());

        bindRMRef(roleMenuRefBean);
    }

    private void bindRMRef(RoleMenuRefBean roleMenuRefBean) {
        List<Integer> menuIdList = roleMenuRefBean.getMenuIdList();
        List<RoleMenuRefBean> roleMenuRefBeanList = new ArrayList<>();
        if (!CollectionsUtils.isEmpty(menuIdList)) {
//            List<RouteBean> routeList = routeDao.getRouteByPage(new RouteBean().setMenuIdList(menuIdList));
//            Map<Integer, List<RouteBean>> map = null;
//            if (!CollectionsUtils.isEmpty(routeList)) {
//                map = routeList.stream().collect(Collectors.groupingBy(RouteBean::getMenuId));
//            }
            for (Integer menuId : menuIdList) {
                RoleMenuRefBean refBean = new RoleMenuRefBean();
//                if (null != map) {
//                    List<RouteBean> routeBeans = map.get(menuId);
//                    if (!CollectionsUtils.isEmpty(routeBeans)) {
//                        refBean.setRouteId(routeBeans.get(0).getId());
//                    }
//                }

                refBean.setCreateTime(new Date());
                refBean.setMenuId(menuId);
                refBean.setRoleId(roleMenuRefBean.getRoleId());
                roleMenuRefBeanList.add(refBean);
            }
        }
        roleMenuRefDao.addRMR(roleMenuRefBeanList);
    }
}
