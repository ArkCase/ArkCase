package com.armedia.acm.services.billing.listener;

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

import com.armedia.acm.activiti.AcmTaskActivitiEvent;
import com.armedia.acm.services.billing.exception.CreateBillingItemException;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

import com.armedia.acm.services.timesheet.service.TimesheetService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingAcmTaskActivitiEventHandler implements ApplicationListener<AcmTaskActivitiEvent>
{

    private AcmTimesheetDao acmTimesheetDao;
    private AcmCostsheetDao acmCostsheetDao;
    private BillingService billingService;
    private TimesheetService timesheetService;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    @Async
    public void onApplicationEvent(AcmTaskActivitiEvent event)
    {

        if (event.getTaskEvent().equals("complete") && event.getProcessVariables().containsKey("reviewOutcome") && event.getProcessVariables().get("reviewOutcome").equals("APPROVE"))
        {
            switch (event.getParentObjectType())
            {
            case TimesheetConstants.OBJECT_TYPE:
                handleTimesheet(event);
                break;
            case CostsheetConstants.OBJECT_TYPE:
                handleCostsheet(event);
            }
        }

    }

    private void handleTimesheet(AcmTaskActivitiEvent event)
    {
        AcmTimesheet timesheet = getAcmTimesheetDao().find(event.getParentObjectId());

        getTimesheetService().accumulateTimesheetByTypeAndChangeCode(timesheet).values().stream().forEach(acmTime -> {
            createBillingItem(event.getUserId(), timesheet.getTitle(), acmTime.getObjectId(), acmTime.getType(), acmTime.getTotalCost());
        });
    }

    private void handleCostsheet(AcmTaskActivitiEvent event)
    {
        AcmCostsheet costsheet = getAcmCostsheetDao().find(event.getParentObjectId());

        createBillingItem(event.getUserId(), costsheet.getTitle(), costsheet.getParentId(), costsheet.getParentType(),
                costsheet.calculateBalance());
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

    /**
     * @return the acmTimesheetDao
     */
    public AcmTimesheetDao getAcmTimesheetDao()
    {
        return acmTimesheetDao;
    }

    /**
     * @param acmTimesheetDao
     *            the acmTimesheetDao to set
     */
    public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao)
    {
        this.acmTimesheetDao = acmTimesheetDao;
    }

    /**
     * @return the acmCostsheetDao
     */
    public AcmCostsheetDao getAcmCostsheetDao()
    {
        return acmCostsheetDao;
    }

    /**
     * @param acmCostsheetDao
     *            the acmCostsheetDao to set
     */
    public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao)
    {
        this.acmCostsheetDao = acmCostsheetDao;
    }

    /**
     * @return the billingService
     */
    public BillingService getBillingService()
    {
        return billingService;
    }

    /**
     * @param billingService
     *            the billingService to set
     */
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
