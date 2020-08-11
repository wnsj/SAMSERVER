package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.MenuBean;
import com.jiubo.sam.bean.MenuCount;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author syl
 * @since 2020-05-21
 */
public interface MenuService extends IService<MenuBean> {

    public MenuCount getAllMenu(MenuBean menuBean);

    public void addMenu(MenuBean menuBean);

    public void patchMenuById(MenuBean menuBean);
    public List<MenuBean> getMenu();
    public Page<MenuBean> getMenuByPage(MenuBean menuBean);
}
