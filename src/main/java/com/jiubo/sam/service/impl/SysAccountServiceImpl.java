package com.jiubo.sam.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.MenuBean;
import com.jiubo.sam.bean.RoleBean;
import com.jiubo.sam.bean.RoleMenuRefBean;
import com.jiubo.sam.bean.SysAccountBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.common.LoginConstant;
import com.jiubo.sam.dao.MenuDao;
import com.jiubo.sam.dao.RoleDao;
import com.jiubo.sam.dao.RoleMenuRefDao;
import com.jiubo.sam.dao.SysAccountDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.SysAccountService;
import com.jiubo.sam.util.CollectionsUtils;
import com.jiubo.sam.util.CookieTools;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mwl
 * @since 2020-02-10
 */
@Service
public class SysAccountServiceImpl implements SysAccountService {

    @Value("${tokenLife}")
    private int tokenLife;

    @Value("${accountLife}")
    private int accountLife;

    @Autowired
    private SysAccountDao accountDao;
    @Autowired
    private RoleMenuRefDao roleMenuRefDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private MenuDao menuDao;

    @Override
    public List<SysAccountBean> queryAccountList(SysAccountBean accountBean) throws Exception {
        return accountDao.queryAccountList(accountBean);
    }

    @Override

    public JSONObject login(String param) throws Exception {
        JSONObject jsonObject = new JSONObject();
        SysAccountBean accountBean = JSONObject.parseObject(param, SysAccountBean.class);
        if (StringUtils.isBlank(accountBean.getAccount())) throw new MessageException("账号不能为空!");
        if (StringUtils.isBlank(accountBean.getPwd())) throw new MessageException("密码不能为空!");
//        String pwd = MD5Util.md5Encrypt32Lower(accountBean.getPwd());
//        accountBean.setPwd(pwd);
        String accessToken = URLEncoder.encode(accountBean.getAccount().concat("604800"), Constant.Charset.UTF8);
        jsonObject.put("accessToken", accessToken);
        List<SysAccountBean> accountBeans = queryAccountList(accountBean);
        SysAccountBean bean = null;
        if (accountBeans.isEmpty()) {
            throw new MessageException("账号或密码错误!");
        } else {
            bean = accountBeans.get(0);
            bean.setPwd("");

            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            jsonObject.put("accountData", bean);
            // 用户权限
//            Integer saId = bean.getSaId();
//            List<AccountRoleRefBean> arRefByAccIdList = accountRoleRefDao.getARRefByAccId(saId);
            // 用户角色
//            if (!CollectionsUtils.isEmpty(arRefByAccIdList)) {
//                List<Integer> roleIdList = arRefByAccIdList.stream().map(AccountRoleRefBean::getRoleId).collect(Collectors.toList());
            List<RoleBean> roleBeanList = roleDao.getRoleByIdList(new RoleBean().setId(bean.getRoleId()));
            bean.setRoleBeanList(roleBeanList);

            // 根据角色查出对应菜单
            List<RoleMenuRefBean> rmrByConditionList = roleMenuRefDao.getRMRByCondition(new RoleMenuRefBean().setRoleId(bean.getRoleId()));

            // 菜单id
            if (!CollectionsUtils.isEmpty(rmrByConditionList)) {
                List<Integer> menuIdList = rmrByConditionList.stream().map(RoleMenuRefBean::getMenuId).distinct().collect(Collectors.toList());
                List<MenuBean> menuList = menuDao.getMenuByRoleIdList(new MenuBean().setIdList(menuIdList));
                if (!CollectionsUtils.isEmpty(menuList)) {
                    List<String> list = menuList.stream().map(MenuBean::getMenuPath).collect(Collectors.toList());
                    jsonObject.put("menuData", list);
//                        Map<Integer, List<MenuBean>> menuMap = menuList.stream().collect(Collectors.groupingBy(MenuBean::getType));
//
//                        Map<Integer, List<MenuBean>> map = menuMap.get(1).stream().collect(Collectors.groupingBy(MenuBean::getParentId));
//
//                        List<MenuBean> menuBeans = map.get(0);
//                        if (!CollectionsUtils.isEmpty(menuBeans)) {
//                            for (MenuBean menuBean : menuBeans) {
//                                List<MenuBean> menuBeanList = this.getChild(menuBean.getId(), menuMap.get(1));
//                                menuBean.setMenuBeanList(menuBeanList);
//                            }
//                            MenuBean menuBean = new MenuBean();
//                            menuBean.setMenuBeanList(menuBeans);
//                            jsonObject.put("menuData", menuBean);
//                        }
//                        List<MenuBean> btnList = menuMap.get(2);
//                        if (!CollectionsUtils.isEmpty(btnList)) {
//                            List<String> btn = btnList.stream().map(MenuBean::getMenuCode).collect(Collectors.toList());
//                            jsonObject.put("btnData",btn);
//                        }else {
//                            jsonObject.put("btnData",null);
//                        }

                }

            }

//            }
            CookieTools.addCookie(response, "accessToken", accessToken, tokenLife);
            CookieTools.addCookie(response, "accountData", URLEncoder.encode(JSON.toJSONString(bean), Constant.Charset.UTF8), accountLife);
        }
        return jsonObject;
    }


    public SysAccountBean addAccount(SysAccountBean accountBean) throws Exception {
        accountBean.setCreateTime(new Date());
        if (StringUtils.isBlank(accountBean.getPwd())) {
            accountBean.setPwd(LoginConstant.DEFAULT);
        }
//        accountBean.setPwd(MD5Util.md5Encrypt32Lower(accountBean.getPwd()));
        List<SysAccountBean> accountBeans = queryAccountList(accountBean);
        if (StringUtils.isBlank(accountBean.getPwd())) throw new MessageException("密码为空");
        if (!CollectionsUtils.isEmpty(accountBeans)) throw new MessageException("该账号已存在");
        accountDao.addAccount(accountBean);

//        List<Integer> roleIdList = accountBean.getRoleIdList();
//        if (!CollectionsUtils.isEmpty(roleIdList)) {
//            List<RoleBean> roleBeanList = roleDao.getRoleByIdList(new RoleBean().setIdList(roleIdList));
//            bindARRef(accountBean, roleIdList);
//            accountBean.setRoleBeanList(roleBeanList);
//        }

        return accountBean;
    }

//    private void bindARRef(SysAccountBean accountBean, List<Integer> roleIdList) {
//        List<AccountRoleRefBean> accountRoleRefBeans = new ArrayList<>();
//        for (Integer roleId : roleIdList) {
//            AccountRoleRefBean accountRoleRefBean = new AccountRoleRefBean();
//            accountRoleRefBean.setAccountId(accountBean.getSaId());
//            accountRoleRefBean.setRoleId(roleId);
//            accountRoleRefBean.setCreateTime(new Date());
//            accountRoleRefBeans.add(accountRoleRefBean);
//        }
//        accountRoleRefDao.addARRef(accountRoleRefBeans);
//    }

    @Override
    public void patchAccount(SysAccountBean accountBean) throws Exception {
        List<SysAccountBean> accountBeans = queryAccountList(new SysAccountBean().setAccount(accountBean.getAccount()));
        if (!CollectionsUtils.isEmpty(accountBeans)) {
            SysAccountBean bean = accountBeans.get(0);
            if (!bean.getSaId().equals(accountBean.getSaId())) throw new MessageException("该用户名已存在");
        }
        if (StringUtils.isBlank(accountBean.getPwd())) {
            accountBean.setPwd(LoginConstant.DEFAULT);
        }
//        accountBean.setPwd(MD5Util.md5Encrypt32Lower(accountBean.getPwd()));
        accountDao.patchAccount(accountBean);
//        List<Integer> roleIdList = accountBean.getRoleIdList();
//        if (!CollectionsUtils.isEmpty(roleIdList)) {
//            //先解绑
//            accountRoleRefDao.deleteARRefByAccountId(accountBean.getSaId());
//            //再绑定
//            bindARRef(accountBean, roleIdList);
//        }
    }

    @Override
    public Page<SysAccountBean> queryAccountByPage(SysAccountBean accountBean) {
        Page<SysAccountBean> page = new Page<>();
        page.setCurrent(StringUtils.isBlank(accountBean.getCurrent()) ? 1L : Long.parseLong(accountBean.getCurrent()));
        page.setSize(StringUtils.isBlank(accountBean.getPageSize()) ? 10L : Long.parseLong(accountBean.getPageSize()));
        List<SysAccountBean> accountBeanList = accountDao.queryAccountList(page, accountBean);
//        List<AccountRoleRefBean> refByAccId = accountRoleRefDao.getARRefByAccId(0);
//        Map<Integer, List<AccountRoleRefBean>> accRoleMap = null;
//        Map<Integer, List<RoleBean>> map = null;
//        if (!CollectionsUtils.isEmpty(refByAccId)) {
//            accRoleMap = refByAccId.stream().collect(Collectors.groupingBy(AccountRoleRefBean::getAccountId));
//            List<Integer> collect = refByAccId.stream().map(AccountRoleRefBean::getRoleId).collect(Collectors.toList());
//            List<RoleBean> roleByIdList = roleDao.getRoleByIdList(new RoleBean().setIdList(collect));
//            map = roleByIdList.stream().collect(Collectors.groupingBy(RoleBean::getId));
//        }
        if (!CollectionsUtils.isEmpty(accountBeanList)) {
            List<Integer> collect = accountBeanList.stream().map(SysAccountBean::getRoleId).distinct().collect(Collectors.toList());
            Map<Integer, List<RoleBean>> map = null;
            List<RoleBean> roleByIdList = roleDao.getRoleByIdList(new RoleBean().setIdList(collect));
            if (!CollectionsUtils.isEmpty(roleByIdList)) {
                map = roleByIdList.stream().collect(Collectors.groupingBy(RoleBean::getId));
            }
            for (SysAccountBean bean : accountBeanList) {
//                if (!CollectionsUtils.isEmpty(accRoleMap)) {
//                    List<AccountRoleRefBean> accountRoleRefBeans = accRoleMap.get(bean.getSaId());
//                    List<String> roleNameList = new ArrayList<>();
//                    List<Integer> roleIdList = new ArrayList<>();
//                    if (!CollectionsUtils.isEmpty(accountRoleRefBeans)) {
//                        for (AccountRoleRefBean accountRoleRefBean : accountRoleRefBeans) {
                if (null != map) {
                    List<RoleBean> roleBeans = map.get(bean.getRoleId());
                    String roleName = roleBeans.get(0).getRoleName();
//                                roleNameList.add(roleName);
//                                roleIdList.add(roleBeans.get(0).getId());
                    bean.setRoleName(roleName);
                }
//                        }
//                    }
//                    if (!CollectionsUtils.isEmpty(roleNameList)) {
//                        bean.setRoleName(StringUtils.join(roleNameList,"、"));
//                    }
//                    bean.setRoleIdList(roleIdList);
//                }
            }
        }
        return page.setRecords(accountBeanList);
    }

    @Override
    public void deleteAccById(SysAccountBean accountBean) {
        accountDao.deleteAccById(accountBean.getSaId());
    }


    /**
     * 递归查找子菜单
     *
     * @param id       当前菜单id
     * @param rootMenu 要查找的列表
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
        for (MenuBean menu : childList) {
            // 递归
            menu.setMenuBeanList(getChild(menu.getId(), rootMenu));
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }


}
