package com.armedia.acm.services.billing.service.impl;

import com.armedia.acm.services.billing.dao.BillingInvoiceDao;
import com.armedia.acm.services.billing.dao.BillingItemDao;
import com.armedia.acm.services.billing.exception.CreateBillingInvoiceException;
import com.armedia.acm.services.billing.exception.CreateBillingItemException;
import com.armedia.acm.services.billing.exception.GetBillingInvoiceException;
import com.armedia.acm.services.billing.exception.GetBillingItemException;
import com.armedia.acm.services.billing.model.BillingEventPublisher;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.model.BillingInvoiceRequest;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.rules.BillingInvoiceBusinessRule;
import com.armedia.acm.services.billing.service.BillingService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.PersistenceException;

import java.util.ArrayList;
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

    private Logger log = LogManager.getLogger(getClass());
    private BillingItemDao billingItemDao;
    private BillingInvoiceDao billingInvoiceDao;
    private BillingInvoiceBusinessRule billingInvoiceBusinessRule;
    private BillingEventPublisher billingEventPublisher;

    /**
     * @return the billingEventPublisher
     */
    public BillingEventPublisher getBillingEventPublisher()
    {
        return billingEventPublisher;
    }

    /**
     * @param billingEventPublisher
     *            the billingEventPublisher to set
     */
    public void setBillingEventPublisher(BillingEventPublisher billingEventPublisher)
    {
        this.billingEventPublisher = billingEventPublisher;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.billing.service.BillingService#getBillingItemsByParentObjectTypeAndId(java.lang.String,
     * java.lang.Long)
     */
    @Override
    public List<BillingItem> getBillingItemsByParentObjectTypeAndId(String parentObjectType, Long parentObjectId)
            throws GetBillingItemException
    {
        log.info("Finding Billing Items for [{}] [{}]", parentObjectType, parentObjectId);
        if (parentObjectId != null && parentObjectType != null)
        {
            try
            {
                List<BillingItem> billingItems = getBillingItemDao().listBillingItems(parentObjectType, parentObjectId);
                log.debug("Billing Item size:{}", billingItems.size());
                return billingItems;
            }
            catch (PersistenceException e)
            {
                throw new GetBillingItemException("Error getting Billing Items", e);
            }
        }
        throw new GetBillingItemException("Could not get Billing Items, missing parentObjectType or parentObjectId");
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.billing.service.BillingService#createBillingItem(com.armedia.acm.services.billing.model.
     * BillingItem)
     */
    @Override
    public BillingItem createBillingItem(BillingItem billingItem) throws CreateBillingItemException
    {
        log.info("Creating Billing Item for [{}] [{}]", billingItem.getParentObjectType(), billingItem.getParentObjectId());
        try
        {
            BillingItem saved = getBillingItemDao().createBillingItem(billingItem);
            getBillingEventPublisher().publishBillingItemCreatedEvent(saved);
            return saved;
        }
        catch (Exception e)
        {
            throw new CreateBillingItemException(
                    String.format("Unable to create Billing Item for [%s] [%d]", billingItem.getParentObjectType(),
                            billingItem.getParentObjectId()),
                    e);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.billing.service.BillingService#getBillingInvoicesByParentObjectTypeAndId(java.lang.
     * String, java.lang.Long)
     */
    @Override
    public List<BillingInvoice> getBillingInvoicesByParentObjectTypeAndId(String parentObjectType, Long parentObjectId)
            throws GetBillingInvoiceException
    {
        return getBillingInvoiceDao().listBillingInvoices(parentObjectType, parentObjectId);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.billing.service.BillingService#getLastBillingInvoice(java.lang.String,
     * java.lang.Long)
     */
    @Override
    public BillingInvoice getLatestBillingInvoice(String parentObjectType, Long parentObjectId) throws GetBillingInvoiceException
    {
        log.info("Getting Latest Billing Invoice for [{}] [{}]", parentObjectType, parentObjectId);
        try
        {
            return getBillingInvoiceDao().getLatestBillingInvoice(parentObjectType, parentObjectId);
        }
        catch (Exception e)
        {
            throw new GetBillingInvoiceException(
                    String.format("Unable to get latest Billing Invoice for [%s] [%d]", parentObjectType, parentObjectId), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.billing.service.BillingService#createBillingInvoice(java.lang.String,
     * java.lang.Long)
     */
    @Override
    public BillingInvoice createBillingInvoice(BillingInvoiceRequest billingInvoiceRequest)
            throws CreateBillingInvoiceException
    {
        List<BillingItem> billingItems = new ArrayList<>();
        try
        {
            billingItems = getBillingItemsByParentObjectTypeAndId(billingInvoiceRequest.getParentObjectType(),
                    billingInvoiceRequest.getParentObjectId());
        }
        catch (GetBillingItemException e)
        {
            throw new CreateBillingInvoiceException(e.getMessage());
        }
        BillingInvoice billingInvoice = new BillingInvoice();
        billingInvoice.setParentObjectType(billingInvoiceRequest.getParentObjectType());
        billingInvoice.setParentObjectId(billingInvoiceRequest.getParentObjectId());
        billingInvoice.setBillingItems(billingItems);
        billingInvoice.setInvoiceNumber("");
        BillingInvoice saved = getBillingInvoiceDao().saveBillingInvoice(billingInvoice);
        getBillingInvoiceBusinessRule().applyRules(saved);
        // for some reason saved is not managed, so have to merge changes again
        BillingInvoice updated = getBillingInvoiceDao().saveBillingInvoice(saved);
        getBillingEventPublisher().publishBillingInvoiceCreatedEvent(updated);
        return updated;
    }
    /**
     * @return the billingItemDao
     */
    public BillingItemDao getBillingItemDao()
    {
        return billingItemDao;
    }

    /**
     * @param billingItemDao
     *            the billingItemDao to set
     */
    public void setBillingItemDao(BillingItemDao billingItemDao)
    {
        this.billingItemDao = billingItemDao;
    }

    /**
     * @return the billingInvoiceDao
     */
    public BillingInvoiceDao getBillingInvoiceDao()
    {
        return billingInvoiceDao;
    }

    /**
     * @param billingInvoiceDao
     *            the billingInvoiceDao to set
     */
    public void setBillingInvoiceDao(BillingInvoiceDao billingInvoiceDao)
    {
        this.billingInvoiceDao = billingInvoiceDao;
    }

    /**
     * @return the billingInvoiceBusinessRule
     */
    public BillingInvoiceBusinessRule getBillingInvoiceBusinessRule()
    {
        return billingInvoiceBusinessRule;
    }

    /**
     * @param billingInvoiceBusinessRule
     *            the billingInvoiceBusinessRule to set
     */
    public void setBillingInvoiceBusinessRule(BillingInvoiceBusinessRule billingInvoiceBusinessRule)
    {
        this.billingInvoiceBusinessRule = billingInvoiceBusinessRule;
    }
}
