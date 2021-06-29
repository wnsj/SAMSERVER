package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.jiubo.sam.bean.DepartmentBean;
import com.jiubo.sam.bean.LogRecordsBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.dao.DepartmentDao;
import com.jiubo.sam.dto.UpdateDepartmentByIdsDto;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.PatientService;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 科室服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentDao, DepartmentBean> implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LogRecordsService logRecordsService;

    @Override
    public List<DepartmentBean> queryDepartment(DepartmentBean departmentBean) throws MessageException {
        return departmentDao.queryDepartment(departmentBean);
    }

    @Override
    public void addDepartment(DepartmentBean departmentBean) throws MessageException {
        List<DepartmentBean> departmentBeans = queryDeptByName(departmentBean);
        if (departmentBeans.size() > 0) throw new MessageException("科室名不能重复!");
        if (departmentDao.insert(departmentBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    public void updateDepartment(DepartmentBean departmentBean) throws MessageException {
        QueryWrapper<DepartmentBean> queryWrapper = new QueryWrapper<DepartmentBean>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "NAME", departmentBean.getName());
        queryWrapper.ne("DEPT_ID", departmentBean.getDeptId());
        List<DepartmentBean> departmentBeans = departmentDao.selectList(queryWrapper);
        if (departmentBeans.size() > 0) throw new MessageException("科室名不能重复!");
        if (departmentDao.updateById(departmentBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartmentById(List<DepartmentBean> departmentBeans) throws Exception {
        PayserviceBean payserviceBean = patientService.selectIsUse(42);
        if (payserviceBean.getIsuse() == "0") throw new MessageException("项目已停用，无法批量操作");
        if (departmentBeans.size()<=0) throw new MessageException("请选择要启动的部门");
        for (int i =0 ; i< departmentBeans.size();i++){
            this.updateDepartment(departmentBeans.get(i));
            if ("1".equals(departmentBeans.get(i).getIsStart())){
                patientService.startUpPayService(new PatientBean()
                        .setDeptId(departmentBeans.get(i).getDeptId())
                        .setAccountId(departmentBeans.get(i).getAccountId())
                        .setIsStart(1)
                );
            }else {
                patientService.startUpPayService(new PatientBean()
                        .setDeptId(departmentBeans.get(i).getDeptId())
                        .setAccountId(departmentBeans.get(i).getAccountId())
                        .setIsStart(0)
                );
            }
        }
        logRecordsService.insertLogRecords(new LogRecordsBean()
                .setHospNum("无")
                .setOperateId(Integer.valueOf(departmentBeans.get(0).getAccountId()))
                .setCreateDate(TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()))
                .setOperateModule("批量操作")
                .setOperateType("修改")
                .setLrComment(departmentBeans.toString())
        );
    }

    @Override
    public List<DepartmentBean> queryDeptByName(DepartmentBean departmentBean) throws MessageException {
        QueryWrapper<DepartmentBean> queryWrapper = new QueryWrapper<DepartmentBean>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "NAME", departmentBean.getName());
        return departmentDao.selectList(queryWrapper);
    }

    @Override
    public List<DepartmentBean> queryArrearsByDept(DepartmentBean departmentBean) throws Exception {
        return departmentDao.queryArrearsByDept(departmentBean);
    }

    @Override
    public void updateDepartmentByIds(UpdateDepartmentByIdsDto updateDepartmentByIdsDto) {


    }
}
