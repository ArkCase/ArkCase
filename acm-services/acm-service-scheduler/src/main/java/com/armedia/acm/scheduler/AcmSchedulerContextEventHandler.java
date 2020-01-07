package com.armedia.acm.scheduler;

/*-
 * #%L
 * ACM Service: Scheduler Service
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

import com.armedia.acm.spring.events.AbstractContextHolderEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

/**
 * Reconfigure the scheduler when a Spring context is added or removed. This lets the scheduler call a scheduled
 * bean which is defined in a child context (that is - a bean defined in a file in .arkcase/acm/spring folder).
 */
public class AcmSchedulerContextEventHandler implements ApplicationListener<AbstractContextHolderEvent>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmScheduler scheduler;

    @Override
    public void onApplicationEvent(AbstractContextHolderEvent abstractContextHolderEvent)
    {
        log.info("Context named [{}] was added or removed - updating scheduler configuration", abstractContextHolderEvent.getContextName());
        getScheduler().updateConfiguration();
    }

    public AcmScheduler getScheduler()
    {
        return scheduler;
    }

    public void setScheduler(AcmScheduler scheduler)
    {
        this.scheduler = scheduler;
    }
}
