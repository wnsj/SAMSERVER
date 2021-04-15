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
 *  服务实现类
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
        QueryWrapper<PrintsBean> printBeanQueryWrapper = new QueryWrapper<>();
        printBeanQueryWrapper.eq("TYPE",printRequest.getType());
        PrintsBean printBean = printsDao.selectOne(printBeanQueryWrapper);
        if(printBean == null){
            String str = String.format("%03d",1);
            printBean = new PrintsBean();
            printBean.setType(printRequest.getType());
            printBean.setCount(str);
            printBean.setModifyTime(LocalDateTime.now());
            printsDao.insert(printBean);
            PrintDetailsBean printDetailsBean = new PrintDetailsBean();
            printDetailsBean.setCode(str);
            printDetailsBean.setPrintId(printBean.getId());
            printDetailsBean.setDetailId(printRequest.getDetailsId());
            printDetailsBean.setModifyTime(LocalDateTime.now());
            printDetailsDao.insert(printDetailsBean);
            count = printDetailsBean.getCode();
        }else {
            QueryWrapper<PrintDetailsBean> printDetailsBeanQueryWrapper = new QueryWrapper<>();
            printDetailsBeanQueryWrapper.eq("PRINT_ID",printBean.getId());
            printDetailsBeanQueryWrapper.eq("DETAIL_ID",printRequest.getDetailsId());
            PrintDetailsBean printDetailsBean = printDetailsDao.selectOne(printDetailsBeanQueryWrapper);
            if(printDetailsBean == null){
                printBean.setModifyTime(LocalDateTime.now());
                printBean.setCount(String.format("%03d",Integer.parseInt(printBean.getCount())+1));
                printDetailsBean = new PrintDetailsBean();
                printDetailsBean.setModifyTime(LocalDateTime.now());
                printDetailsBean.setDetailId(printRequest.getDetailsId());
                printDetailsBean.setPrintId(printBean.getId());
                printDetailsBean.setCode(printBean.getCount());
                printsDao.updateById(printBean);
                printDetailsDao.insert(printDetailsBean);
                count = printDetailsBean.getCode();
            }else {
                printDetailsBean.setModifyTime(LocalDateTime.now());
                printDetailsDao.updateById(printDetailsBean);
                count = printDetailsBean.getCode();
            }
        }
        return count;
    }

}
