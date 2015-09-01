package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.AcmQueueDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;

import java.util.List;

/**
 * Created by nebojsha on 31.08.2015.
 */
public class AcmQueueServiceImpl implements AcmQueueService
{

    private AcmQueueDao acmQueueDao;

    @Override
    public List<AcmQueue> listAllQueues()
    {
        return acmQueueDao.findAll();
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }
}
