package com.armedia.acm.activiti;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmActivitiTaskEventListener implements ApplicationListener<AcmTaskActivitiEvent>
{
    private int timesCalled;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void onApplicationEvent(AcmTaskActivitiEvent event)
    {
        log.info("Got an event: " + event.getEventType());
        ++timesCalled;
    }

    /**
     * To be called from the Activiti business process, to demonstrate we can call Spring beans from Activiti
     * 
     * @param message
     */
    public void logMessage(String message, String moreInfo, String approver)
    {
        log.info("Got a message: " + message);
        log.info("More info: " + moreInfo);

        log.info("approver: " + approver);
    }

    public int getTimesCalled()
    {
        return timesCalled;
    }

    public void reset()
    {
        timesCalled = 0;
    }
}
