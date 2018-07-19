package com.armedia.acm.services.billing.listener;

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
                        getBillingService().createBillingItem(populateBillingItem(event.getUserId(), timesheet.getTitle(), row.getObjectId(),
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
                    getBillingService().createBillingItem(populateBillingItem(event.getUserId(), costsheet.getTitle(), costsheet.getParentId(),
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
