package com.jiubo.sam.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @desc:
 * @date: 2019-08-15 14:58
 * @author: dx
 * @version: 1.0
 */
@Component
public class InstantContextAfterProcessor implements ApplicationContextAware {

    public static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static <T> T getService(String name, Class<T> claz) {
        if (applicationContext == null) return null;
        return (T) applicationContext.getBean(name);
    }
}
