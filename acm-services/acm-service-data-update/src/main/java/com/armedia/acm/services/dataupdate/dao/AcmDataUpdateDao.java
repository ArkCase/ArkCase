package com.armedia.acm.services.dataupdate.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.dataupdate.model.AcmDataUpdateExecutorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AcmDataUpdateDao extends AcmAbstractDao<AcmDataUpdateExecutorLog>
{
    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(AcmDataUpdateDao.class);

    @Override
    protected Class<AcmDataUpdateExecutorLog> getPersistenceClass()
    {
        return AcmDataUpdateExecutorLog.class;
    }
}
