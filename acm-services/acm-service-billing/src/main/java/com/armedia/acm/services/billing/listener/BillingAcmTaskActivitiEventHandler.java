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
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Async
    public void onApplicationEvent(AcmTaskActivitiEvent event)
    {

        if (event.getTaskEvent().equals("complete"))
        {
            if (event.getParentObjectType().equals(TimesheetConstants.OBJECT_TYPE))
            {
                AcmTimesheet timesheet = getAcmTimesheetDao().find(event.getParentObjectId());
                Map<String, AcmTime> timesheetRowPerTypeAndChangeCode = new HashMap<>();
                for (AcmTime acmTime : timesheet.getTimes())
                {
                    if (acmTime.getType().equals("CASE_FILE") || acmTime.getType().equals("COMPLAINT"))
                        if (timesheetRowPerTypeAndChangeCode.containsKey(acmTime.getType() + acmTime.getObjectId()))
                        {
                            AcmTime rowPerDay = timesheetRowPerTypeAndChangeCode.get(acmTime.getType() + acmTime.getObjectId());
                            rowPerDay.setTotalCost(rowPerDay.getTotalCost() + acmTime.getTotalCost());
                        }
                        else
                        {
                            timesheetRowPerTypeAndChangeCode.put(acmTime.getType() + acmTime.getObjectId(), acmTime);
                        }
                }
                timesheetRowPerTypeAndChangeCode.values().stream().forEach(row -> {
                    try
                    {
                        getBillingService()
                                .createBillingItem(populateBillingItem(event.getUserId(), timesheet.getTitle(), row.getObjectId(),
                                        row.getType(), row.getTotalCost()));
                    }
                    catch (CreateBillingItemException e)
                    {
                        log.error("Can not add Billing Item for [{}]", timesheet.getTitle());
                    }
                });
            }
            else if (event.getParentObjectType().equals(CostsheetConstants.OBJECT_TYPE))
            {
                AcmCostsheet costsheet = getAcmCostsheetDao().find(event.getParentObjectId());
                double balance = 0.0;
                for (AcmCost acmCost : costsheet.getCosts())
                {
                    if (acmCost.getValue() > 0)
                    {
                        balance += acmCost.getValue();
                    }
                }
                try
                {
                    getBillingService()
                            .createBillingItem(populateBillingItem(event.getUserId(), costsheet.getTitle(), costsheet.getParentId(),
                                    costsheet.getParentType(), balance));
                }
                catch (CreateBillingItemException e)
                {
                    log.error("Can not add Billing Item for [{}]", costsheet.getTitle());
                }
            }
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

}
