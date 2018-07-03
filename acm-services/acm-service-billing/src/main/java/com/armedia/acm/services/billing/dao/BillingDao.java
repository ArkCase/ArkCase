package com.armedia.acm.services.billing.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.billing.model.BillingItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingDao extends AcmAbstractDao<BillingItem>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<BillingItem> getPersistenceClass()
    {
        return BillingItem.class;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}
