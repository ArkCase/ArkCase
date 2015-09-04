package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

/**
 * Created by nebojsha on 31.08.2015.
 */
public class AcmQueueDao extends AcmAbstractDao<AcmQueue>
{

    @Override
    protected Class<AcmQueue> getPersistenceClass()
    {
        return AcmQueue.class;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AcmQueue findByName(String queueName)
    {
        TypedQuery<AcmQueue> queueQuery = getEm().createQuery(
                "SELECT e FROM " + getPersistenceClass().getSimpleName() + " e WHERE e.name = :name",
                AcmQueue.class);

        queueQuery.setParameter("name", queueName);

        AcmQueue retval = queueQuery.getSingleResult();

        return retval;
    }
}
