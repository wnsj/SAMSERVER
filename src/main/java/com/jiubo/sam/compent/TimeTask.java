package com.jiubo.sam.compent;

import com.jiubo.sam.dao.PrintsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimeTask {

    @Autowired
    private PrintsDao printsDao;

    @Scheduled(cron = "0 0 0 * * ?")
    public void truncatePrints(){
        printsDao.truncatePrints();
    }
}
