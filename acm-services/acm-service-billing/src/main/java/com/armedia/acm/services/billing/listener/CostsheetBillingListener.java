package com.armedia.acm.services.billing.listener;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.services.billing.exception.CreateBillingItemException;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.AcmCostsheetEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

public class CostsheetBillingListener implements ApplicationListener<AcmCostsheetEvent>
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmCostsheetDao acmCostsheetDao;
    private BillingService billingService;

    @Override
    public void onApplicationEvent(AcmCostsheetEvent acmCostsheetEvent)
    {
        if (acmCostsheetEvent != null && !acmCostsheetEvent.isStartWorkflow())
        {
            AcmCostsheet costsheet = (AcmCostsheet)acmCostsheetEvent.getSource();
            if("FINAL".equals(costsheet.getStatus()))
            {
                generateBillingItems(acmCostsheetEvent);
            }
        }
    }

    private void generateBillingItems(AcmCostsheetEvent acmCostsheetEvent)
    {
        AcmCostsheet costsheet = (AcmCostsheet)acmCostsheetEvent.getSource();
        BillingItem costsheetBillingItem = populateBillingItem(acmCostsheetEvent.getUserId(), costsheet.getTitle(), costsheet.getParentId(), costsheet.getParentType(), costsheet.calculateBalance(), BillingConstants.BILLING_ITEM_TYPE_COSTSHEET);

        try
        {
            getBillingService().createBillingItem(costsheetBillingItem);
        }
        catch (CreateBillingItemException e)
        {
            log.error("Could not create Billing Item for objectType = [{}] and objectId = [{}]", costsheet.getParentType(),
                    costsheet.getParentId());
        }
    }

    private BillingItem populateBillingItem(String creator, String itemDescription, Long parentObjectId, String parentObjectType, Double itemAmount, String itemType)
    {
        BillingItem billingItem = new BillingItem();
        billingItem.setCreator(creator);
        billingItem.setModifier(creator);
        billingItem.setItemDescription(itemDescription);
        billingItem.setParentObjectId(parentObjectId);
        billingItem.setParentObjectType(parentObjectType);
        billingItem.setItemAmount(itemAmount);
        billingItem.setItemType(itemType);
        return billingItem;
    }

    public AcmCostsheetDao getAcmCostsheetDao()
    {
        return acmCostsheetDao;
    }

    public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao)
    {
        this.acmCostsheetDao = acmCostsheetDao;
    }

    public BillingService getBillingService()
    {
        return billingService;
    }

    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }
}
