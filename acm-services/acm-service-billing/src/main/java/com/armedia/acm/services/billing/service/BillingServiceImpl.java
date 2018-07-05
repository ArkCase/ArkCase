package com.armedia.acm.services.billing.service;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.billing.dao.BillingDao;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;

import java.util.List;

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
/**
 * @author sasko.tanaskoski
 *
 */
public class BillingServiceImpl implements BillingService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private BillingDao billingDao;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.billing.service.BillingService#getBillingItemsByObjectTypeAndId(java.lang.String,
     * java.lang.Long)
     */
    @Override
    public List<BillingItem> getBillingItemsByObjectTypeAndId(String objectType, Long objectId) throws AcmListObjectsFailedException
    {
        log.info("Finding Billing Items");
        if (objectId != null && objectType != null)
        {
            try
            {
                List<BillingItem> billingItems = getBillingDao().listBillingItems(objectType, objectId);
                log.debug("Billing Item size:{}", billingItems.size());
                return billingItems;
            }
            catch (PersistenceException e)
            {
                throw new AcmListObjectsFailedException(BillingConstants.OBJECT_TYPE, e.getMessage(), e);
            }
        }
        throw new AcmListObjectsFailedException(BillingConstants.OBJECT_TYPE, "wrong input", null);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.billing.service.BillingService#addBillingItem(com.armedia.acm.services.billing.model.
     * BillingItem)
     */
    @Override
    public BillingItem addBillingItem(BillingItem billingItem)
    {
        return billingDao.addBilligItem(billingItem);
    }

    /**
     * @return the billingDao
     */
    public BillingDao getBillingDao()
    {
        return billingDao;
    }

    /**
     * @param billingDao
     *            the billingDao to set
     */
    public void setBillingDao(BillingDao billingDao)
    {
        this.billingDao = billingDao;
    }

}
