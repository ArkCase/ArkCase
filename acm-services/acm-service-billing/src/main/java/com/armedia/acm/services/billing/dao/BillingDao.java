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
public class BillingDao extends AcmAbstractDao<BillingItem>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<BillingItem> getPersistenceClass()
    {
        return BillingItem.class;
    }

    public List<BillingItem> listBillingItems(String objectType, Long objectId)
    {

        TypedQuery<BillingItem> billingItem = getEntityManager().createQuery(
                "SELECT billingItem " +
                        "FROM BillingItem billingItem " +
                        "WHERE billingItem.parentObjectType = :objectType AND " +
                        "billingItem.parentObjectId = :objectId " +
                        "ORDER BY billingItem.itemNumber",
                BillingItem.class);

        billingItem.setParameter("objectType", objectType.toUpperCase());
        billingItem.setParameter("objectId", objectId);

        List<BillingItem> billingItems = billingItem.getResultList();
        if (null == billingItems)
        {
            billingItems = new ArrayList<BillingItem>();
        }
        return billingItems;
    }

    @Transactional
    public BillingItem addBilligItem(BillingItem billingItem)
    {
        billingItem.setItemNumber(getNextItemNumber(billingItem.getParentObjectType(), billingItem.getParentObjectId()));
        BillingItem saved = save(billingItem);
        return saved;
    }

    private int getNextItemNumber(String objectType, Long objectId)
    {
        String queryText = "SELECT MAX(billingItem.itemNumber) FROM BillingItem billingItem WHERE billingItem.parentObjectType = :objectType AND billingItem.parentObjectId = :objectId";
        Query query = getEm().createQuery(queryText);
        query.setParameter("objectType", objectType.toUpperCase());
        query.setParameter("objectId", objectId);
        Integer currentItemNumber = (Integer) query.getSingleResult();
        return currentItemNumber != null ? currentItemNumber + 1 : 1;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}
