package gov.foia.service;

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
    protected Integer getMaxDaysInQueueProperty()
    {
        return getFoiaConfigurationService().getFoiaConfig().getMaxDaysInBillingQueue();
    }

    @Override
    protected Boolean getPurgeRequestWhenInHoldEnabled()
    {
        return getFoiaConfigurationService().getFoiaConfig().getPurgeRequestEnabled();
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
