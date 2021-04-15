package com.jiubo.sam.service;

import com.jiubo.sam.bean.PrintsBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.request.PrintRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dx
 * @since 2021-04-15
 */
public interface PrintsService extends IService<PrintsBean> {

    String addPrint(PrintRequest printRequest);

}
