package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.service.DataAccessDefaultService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by armdev on 7/9/14.
 */
public class DataAccessDefaultLauncher implements ApplicationContextAware
{
    private DataAccessDefaultService dataAccessDefaultService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        getDataAccessDefaultService().persistDefaultDataAccessControls(applicationContext);
    }

    public DataAccessDefaultService getDataAccessDefaultService()
    {
        return dataAccessDefaultService;
    }

    public void setDataAccessDefaultService(DataAccessDefaultService dataAccessDefaultService)
    {
        this.dataAccessDefaultService = dataAccessDefaultService;
    }
}
