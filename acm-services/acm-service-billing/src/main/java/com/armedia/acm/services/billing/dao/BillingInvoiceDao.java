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
import com.armedia.acm.services.billing.model.BillingItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceDao extends AcmAbstractDao<BillingInvoice>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

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
                "ORDER BY billingInvoice.invoiceNumber";

        TypedQuery<BillingInvoice> billingInvoice = getEntityManager().createQuery(queryText, BillingInvoice.class);

        billingInvoice.setParameter("parentObjectType", parentObjectType.toUpperCase());
        billingInvoice.setParameter("parentObjectId", parentObjectId);

        List<BillingInvoice> billingInvoices = billingInvoice.getResultList();
        if (null == billingInvoices)
        {
            billingInvoices = new ArrayList<BillingInvoice>();
        }
        return billingInvoices;
    }

    @Transactional
    public BillingInvoice createBillingInvoice(String parentObjectType, Long parentObjectId, String parentObjectNumber,
            List<BillingItem> billingItems)
    {
        BillingInvoice billingInvoice = new BillingInvoice();
        billingInvoice.setParentObjectType(parentObjectType);
        billingInvoice.setParentObjectId(parentObjectId);
        billingInvoice.setBillingItems(billingItems);
        billingInvoice.setInvoiceNumber(getNextInvoiceNumber(parentObjectType, parentObjectId, parentObjectNumber));
        BillingInvoice saved = save(billingInvoice);
        return saved;
    }

    private String getNextInvoiceNumber(String parentObjectType, Long parentObjectId, String parentObjectNumber)
    {
        String queryText = "SELECT COUNT(billingInvoice.invoiceNumber) " +
                "FROM BillingInvoice billingInvoice " +
                "WHERE billingInvoice.parentObjectType = :parentObjectType " +
                "AND billingInvoice.parentObjectId = :parentObjectId";

        Query query = getEm().createQuery(queryText);
        query.setParameter("parentObjectType", parentObjectType.toUpperCase());
        query.setParameter("parentObjectId", parentObjectId);
        String invoicePrefix = parentObjectNumber != null ? parentObjectNumber + "_" : "";
        Long invoiceCount = (Long) query.getSingleResult();
        return invoicePrefix + (invoiceCount + 1);
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}
