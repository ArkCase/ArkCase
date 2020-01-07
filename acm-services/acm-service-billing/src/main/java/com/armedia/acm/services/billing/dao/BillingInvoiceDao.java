package com.armedia.acm.services.billing.dao;

/*-
 * #%L
 * ACM Service: Billing
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.billing.model.BillingInvoice;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceDao extends AcmAbstractDao<BillingInvoice>
{
    private final Logger log = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<BillingInvoice> getPersistenceClass()
    {
        return BillingInvoice.class;
    }

    public List<BillingInvoice> listBillingInvoices(String parentObjectType, Long parentObjectId)
    {

        String queryText = "SELECT billingInvoice " +
                "FROM BillingInvoice billingInvoice " +
                "WHERE billingInvoice.parentObjectType = :parentObjectType " +
                "AND billingInvoice.parentObjectId = :parentObjectId " +
                "ORDER BY billingInvoice.id DESC";

        TypedQuery<BillingInvoice> query = getEntityManager().createQuery(queryText, BillingInvoice.class);

        query.setParameter("parentObjectType", parentObjectType.toUpperCase());
        query.setParameter("parentObjectId", parentObjectId);

        List<BillingInvoice> billingInvoices = query.getResultList();
        if (null == billingInvoices)
        {
            billingInvoices = new ArrayList<>();
        }
        return billingInvoices;
    }

    public BillingInvoice getLatestBillingInvoice(String parentObjectType, Long parentObjectId)
    {

        String queryText = "SELECT billingInvoice " +
                "FROM BillingInvoice billingInvoice " +
                "WHERE billingInvoice.id = (" +
                "SELECT MAX(billingInvoice.id) FROM BillingInvoice billingInvoice " +
                "WHERE billingInvoice.parentObjectType = :parentObjectType " +
                "AND billingInvoice.parentObjectId = :parentObjectId)";

        TypedQuery<BillingInvoice> query = getEntityManager().createQuery(queryText, BillingInvoice.class);

        query.setParameter("parentObjectType", parentObjectType.toUpperCase());
        query.setParameter("parentObjectId", parentObjectId);

        return query.getSingleResult();
    }

    @Transactional
    public BillingInvoice saveBillingInvoice(BillingInvoice billingInvoice)
    {
        return save(billingInvoice);
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}
