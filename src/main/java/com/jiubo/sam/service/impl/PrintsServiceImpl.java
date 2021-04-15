package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.PrintDetailsBean;
import com.jiubo.sam.bean.PrintsBean;
import com.jiubo.sam.dao.PrintDetailsDao;
import com.jiubo.sam.dao.PrintsDao;
import com.jiubo.sam.request.PrintRequest;
import com.jiubo.sam.service.PrintsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dx
 * @since 2021-04-15
 */
@Service
public class PrintsServiceImpl extends ServiceImpl<PrintsDao, PrintsBean> implements PrintsService {

    @Autowired
    private PrintsDao printsDao;

    @Autowired
    private PrintDetailsDao printDetailsDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addPrint(PrintRequest printRequest) {
        String count = "";
        QueryWrapper<PrintDetailsBean> printDetailsBeanQueryWrapper = new QueryWrapper<>();
        printDetailsBeanQueryWrapper.eq("PRINT_ID", printRequest.getType());
        printDetailsBeanQueryWrapper.eq("DETAIL_ID", printRequest.getDetailsId());
        PrintDetailsBean printDetailsBean = printDetailsDao.selectOne(printDetailsBeanQueryWrapper);
        if (printDetailsBean == null) {
            return "false";
        } else {
            count = printDetailsBean.getCode();
            return count;
        }
    }

}
