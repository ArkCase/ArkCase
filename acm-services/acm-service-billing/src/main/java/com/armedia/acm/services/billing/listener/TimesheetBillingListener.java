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
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.AcmTimesheetEvent;
import com.armedia.acm.services.timesheet.service.TimesheetService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class TimesheetBillingListener implements ApplicationListener<AcmTimesheetEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmTimesheetDao acmTimesheetDao;
    private BillingService billingService;
    private TimesheetService timesheetService;

    @Override
    public void onApplicationEvent(AcmTimesheetEvent acmTimesheetEvent)
    {
        if (acmTimesheetEvent != null && !acmTimesheetEvent.isStartWorkflow())
        {
            AcmTimesheet timesheet = (AcmTimesheet) acmTimesheetEvent.getSource();
            if ("FINAL".equals(timesheet.getStatus()))
            {
                generateBillingItems(acmTimesheetEvent);
            }
        }
    }

    private void generateBillingItems(AcmTimesheetEvent event)
    {
        AcmTimesheet timesheet = (AcmTimesheet) event.getSource();

        getTimesheetService().accumulateTimesheetByTypeAndChangeCode(timesheet).values().forEach(acmTime -> {
            createBillingItem(event.getUserId(), timesheet.getTitle(), acmTime.getObjectId(), acmTime.getType(), acmTime.getTotalCost());
        });
    }

    private void createBillingItem(String userId, String title, Long parentObjectId, String parentObjectType, double balance)
    {
        try
        {
            getBillingService().createBillingItem(populateBillingItem(userId, title, parentObjectId, parentObjectType, balance));
        }
        catch (CreateBillingItemException e)
        {
            log.error("Can not add Billing Item from [{}] for objectType = [{}] and objectId = [{}]", title, parentObjectType,
                    parentObjectId);
        }
    }

    private BillingItem populateBillingItem(String creator, String itemDescription, Long parentObjectId, String parentObjectType,
            Double itemAmount)
    {
        BillingItem billingItem = new BillingItem();
        billingItem.setCreator(creator);
        billingItem.setModifier(creator);
        billingItem.setItemDescription(itemDescription);
        billingItem.setParentObjectId(parentObjectId);
        billingItem.setParentObjectType(parentObjectType);
        billingItem.setItemAmount(itemAmount);
        return billingItem;
    }

    public AcmTimesheetDao getAcmTimesheetDao()
    {
        return acmTimesheetDao;
    }

    public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao)
    {
        this.acmTimesheetDao = acmTimesheetDao;
    }

    public BillingService getBillingService()
    {
        return billingService;
    }

    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }

    public TimesheetService getTimesheetService()
    {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService)
    {
        this.timesheetService = timesheetService;
    }
}
