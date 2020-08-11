package com.jiubo.sam.service.impl;

import com.jiubo.sam.bean.EmpDepartmentRefBean;
import com.jiubo.sam.dao.EmpDepartmentRefDao;
import com.jiubo.sam.service.EmpDepartmentRefService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.util.CollectionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author syl
 * @since 2020-08-10
 */
@Service
public class EmpDepartmentRefServiceImpl extends ServiceImpl<EmpDepartmentRefDao, EmpDepartmentRefBean> implements EmpDepartmentRefService {

    @Autowired
    private EmpDepartmentRefDao empDepartmentRefDao;

    @Override
    public List<Long> getEdRefByEmpId(EmpDepartmentRefBean empDepartmentRefBean) {
        List<EmpDepartmentRefBean> edRefByEmpIdList = empDepartmentRefDao.getEdRefByEmpId(empDepartmentRefBean.getEmpId());
        if (!CollectionsUtils.isEmpty(edRefByEmpIdList)) {
            return edRefByEmpIdList.stream().map(EmpDepartmentRefBean::getDeptId).collect(Collectors.toList());
        }
        return null;
    }
}
