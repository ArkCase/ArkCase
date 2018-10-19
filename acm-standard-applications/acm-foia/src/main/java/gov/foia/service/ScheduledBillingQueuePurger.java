package gov.foia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import gov.foia.model.FOIARequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 25, 2016
 */
public class ScheduledBillingQueuePurger extends AbstractScheduledQueuePurger
{

    private static final String PROCESS_USER = "BILLING_QUEUE_PURGER";
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * @return the log
     */
    @Override
    protected Logger getLog()
    {
        return log;
    }

    @Override
    protected String getClassName()
    {
        return getClass().getName();
    }

    /**
     * @return
     */
    @Override
    protected String getMaxDaysInQueueProperty()
    {
        return "maxDaysInBillingQueue";
    }

    @Override
    protected List<FOIARequest> getAllRequestsInQueueBefore(LocalDate date)
    {
        return getRequestDao().getAllRequestsInBillingBefore(LocalDate.now().minusDays(getMaxDaysInQueue()));
    }

    @Override
    protected String getProcessUser()
    {
        return PROCESS_USER;
    }

    /**
     * @return
     */
    @Override
    protected String getBusinessProcessName()
    {
        return "foia-extension-hold-process";
    }

}
