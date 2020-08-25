package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class ScheduledHoldQueuePurger extends AbstractScheduledQueuePurger
{

    private static final String PROCESS_USER = "HOLD_QUEUE_PURGER";
    private transient final Logger log = LogManager.getLogger(getClass());

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
        return getSARConfigurationService().getSARConfig().getMaxDaysInHoldQueue();
    }

    @Override
    protected Boolean getPurgeRequestEnabled()
    {
        return getSARConfigurationService().getSARConfig().getPurgeRequestWhenInHoldEnabled();
    }

    @Override
    protected List<SubjectAccessRequest> getAllRequestsInQueueBefore(LocalDate date)
    {
        return getRequestDao().getAllRequestsInHoldBefore(LocalDate.now().minusDays(getMaxDaysInQueue()));
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
        return "privacy-extension-delete-process";
    }

}
