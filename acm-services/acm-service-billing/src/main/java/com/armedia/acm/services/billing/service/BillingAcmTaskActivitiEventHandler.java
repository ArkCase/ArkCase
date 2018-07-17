package com.armedia.acm.services.billing.service;

import com.armedia.acm.activiti.AcmTaskActivitiEvent;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.billing.model.BillingItem;
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
    public void onApplicationEvent(AcmTaskActivitiEvent event)
    {

        if (event.getTaskEvent().equals("complete"))
        {
            if (event.getParentObjectType().equals(TimesheetConstants.OBJECT_TYPE))
            {
                AcmTimesheet timesheet = getAcmTimesheetDao().find(event.getParentObjectId());
                for (AcmTime acmTime : timesheet.getTimes())
                {
                    if (acmTime.getTotalCost() > 0)
                    {
                        BillingItem billingItem = new BillingItem();
                        billingItem.setCreator(event.getUserId());
                        billingItem.setParentObjectId(acmTime.getObjectId());
                        billingItem.setParentObjectType(acmTime.getType());
                        billingItem.setItemDescription(timesheet.getTitle());
                        billingItem.setItemAmount(acmTime.getTotalCost());
                        try
                        {
                            getBillingService().addBillingItem(billingItem);
                        }
                        catch (AcmUserActionFailedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            else if (event.getParentObjectType().equals(CostsheetConstants.OBJECT_TYPE))
            {
                AcmCostsheet costsheet = getAcmCostsheetDao().find(event.getParentObjectId());
                BillingItem billingItem = new BillingItem();
                billingItem.setCreator(event.getUserId());
                billingItem.setParentObjectId(new Long(costsheet.getParentId()));
                billingItem.setParentObjectType(costsheet.getParentType());
                billingItem.setItemDescription(costsheet.getTitle());
                double balance = 0.0;
                for (AcmCost acmCost : costsheet.getCosts())
                {
                    if (acmCost.getValue() > 0)
                    {
                        balance += acmCost.getValue();
                    }
                }
                billingItem.setItemAmount(balance);
                try
                {
                    getBillingService().addBillingItem(billingItem);
                }
                catch (AcmUserActionFailedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

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
