package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.MenuBean;
import com.jiubo.sam.bean.MenuCount;
import com.jiubo.sam.bean.RoleMenuRefBean;
import com.jiubo.sam.dao.MenuDao;
import com.jiubo.sam.dao.RoleMenuRefDao;
import com.jiubo.sam.service.MenuService;
import com.jiubo.sam.util.CollectionsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author syl
 * @since 2020-05-21
 */
@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuDao, MenuBean> implements MenuService {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private RoleMenuRefDao roleMenuRefDao;
    @Override
    public MenuCount getAllMenu(MenuBean menuBean) {
        MenuCount menuCount = new MenuCount();
        // 所有菜单
        List<MenuBean> menuByRoleIdList = menuDao.getMenuByRoleIdList(new MenuBean());
        if (!CollectionsUtils.isEmpty(menuByRoleIdList)) {
            Map<Integer, List<MenuBean>> collect = menuByRoleIdList.stream().collect(Collectors.groupingBy(MenuBean::getParentId));
            List<MenuBean> menuBeans = collect.get(0);
            List<Integer> collect1 = menuBeans.stream().map(MenuBean::getId).collect(Collectors.toList());
            menuCount.setFirstIdList(collect1);
            for (MenuBean bean :menuBeans) {
                List<MenuBean> child = this.getChild(bean.getId(), menuByRoleIdList);
                bean.setMenuBeanList(child);
            }
            menuCount.setMenuBeanList(menuBeans);
        }
        // 选中的菜单
        List<RoleMenuRefBean> rmrByCondition = roleMenuRefDao.getRMRByCondition(new RoleMenuRefBean().setRoleId(menuBean.getRoleId()));
        if (!CollectionsUtils.isEmpty(rmrByCondition)) {
            List<Integer> idList = new ArrayList<>();
            List<Integer> list = rmrByCondition.stream().map(RoleMenuRefBean::getMenuId).collect(Collectors.toList());
            List<MenuBean> menuList = menuDao.getMenuByRoleIdList(new MenuBean().setIdList(list));
            List<MenuBean> allMenuList = menuDao.getMenuByRoleIdList(new MenuBean());
            if (!CollectionsUtils.isEmpty(menuList)) {
                Map<Integer, List<MenuBean>> collect = menuList.stream().collect(Collectors.groupingBy(MenuBean::getParentId));
                List<MenuBean> menuBeans1 = collect.get(0);
                for (MenuBean bean :menuBeans1) {
                    this.getCheckedChild(bean.getId(), menuList,allMenuList,idList);
                }
                menuCount.setCheckedIdList(idList);
            }
        }
        return menuCount;
    }

    @Override
    public void addMenu(MenuBean menuBean) {
        menuBean.setCreateTime(new Date());
        menuDao.addMenu(menuBean);
    }

    @Override
    public void patchMenuById(MenuBean menuBean) {
        menuDao.patchMenuById(menuBean);
    }

    @Override
    public List<MenuBean> getMenu() {
        return menuDao.getMenuByRoleIdList(new MenuBean());
    }

    @Override
    public Page<MenuBean> getMenuByPage(MenuBean menuBean) {
        Page<MenuBean> page = new Page<>();
        page.setCurrent(StringUtils.isBlank(menuBean.getCurrent()) ? 1L : Long.parseLong(menuBean.getCurrent()));
        page.setSize(StringUtils.isBlank(menuBean.getPageSize()) ? 10L : Long.parseLong(menuBean.getPageSize()));
        List<MenuBean> menuByRoleIdList = menuDao.getMenuByRoleIdList(page, menuBean);
        if (!CollectionsUtils.isEmpty(menuByRoleIdList)) {
            List<MenuBean> list = menuDao.getMenuByRoleIdList(new MenuBean());
            Map<Integer, List<MenuBean>> collect = list.stream().collect(Collectors.groupingBy(MenuBean::getId));
            for (MenuBean menuBean1 : menuByRoleIdList) {
                List<MenuBean> menuBeans = collect.get(menuBean1.getParentId());
                if (!CollectionsUtils.isEmpty(menuBeans)) {
                    menuBean1.setParentName(menuBeans.get(0).getMenuName());
                } else {
                    menuBean1.setParentName("-");
                }

                if (menuBean1.getState() == 1) {
                    menuBean1.setStateLabel("启用");
                } else {
                    menuBean1.setStateLabel("禁用");
                }
            }
        }
        return page.setRecords(menuByRoleIdList);
    }

    /**
     * 递归查找子菜单
     *
     * @param id
     *            当前菜单id
     * @param rootMenu
     *            要查找的列表
     * @return
     */
    private List<MenuBean> getChild(Integer id, List<MenuBean> rootMenu) {
        // 子菜单
        List<MenuBean> childList = new ArrayList<>();
        for (MenuBean menu : rootMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (0 != menu.getParentId()) {
                if (menu.getParentId() == id) {
                    childList.add(menu);
                }
            }
        }
        // 把子菜单的子菜单再循环一遍
        for (MenuBean menu : childList) {// 没有url子菜单还有子菜单
            // 递归
            menu.setMenuBeanList(getChild(menu.getId(), rootMenu));
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }


    private List<MenuBean> getCheckedChild(Integer id, List<MenuBean> rootMenu,List<MenuBean> allMenuList,List<Integer> idList) {
        List<MenuBean> childSourceList = new ArrayList<>();

        for (MenuBean menu : allMenuList) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (0 != menu.getParentId()) {
                if (menu.getParentId() == id) {
                    childSourceList.add(menu);
                }
            }
        }

        // 子菜单
        List<MenuBean> childList = new ArrayList<>();

        for (MenuBean menu : rootMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (0 != menu.getParentId()) {
                if (menu.getParentId() == id) {
                    childList.add(menu);
                }
            }
        }

        // 把子菜单的子菜单再循环一遍

        for (MenuBean menu : childList) {// 没有url子菜单还有子菜单
            // 递归
            menu.setMenuBeanList(getCheckedChild(menu.getId(), rootMenu,allMenuList,idList));
            if (CollectionsUtils.isEmpty(menu.getMenuBeanList())) {
                idList.add(menu.getId());
            }
        } // 递归退出条件

        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }
}
