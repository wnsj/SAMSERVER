package com.jiubo.sam.service.impl;

import com.jiubo.sam.dao.CommonDao;
import com.jiubo.sam.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @desc:
 * @date: 2019-09-07 13:45
 * @author: dx
 * @version: 1.0
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonDao commonDao;

    @Override
    public Date getDBTime() {
        return commonDao.getDBTime();
    }
}
