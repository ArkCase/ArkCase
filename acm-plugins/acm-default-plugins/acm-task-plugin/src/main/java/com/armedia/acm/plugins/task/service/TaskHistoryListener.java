/**
 *
 */
package com.armedia.acm.plugins.task.service;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

/**
 * @author riste.tutureski
 */
public class TaskHistoryListener implements ApplicationListener<AcmApplicationTaskEvent>
{

    private static final String OBJECT_TYPE = "TASK";
    private final Logger LOG = LogManager.getLogger(getClass());
    private AcmObjectHistoryService acmObjectHistoryService;

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent event)
    {
        LOG.debug("Task event raised. Start adding it to the object history ...");

        if (event != null)
        {
            boolean execute = checkExecution(event.getEventType());

            if (execute)
            {
                AcmTask task = (AcmTask) event.getSource();

                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), task, task.getId(), OBJECT_TYPE,
                        event.getEventDate(), event.getIpAddress(), event.isSucceeded());
            }
        }
    }

    private boolean checkExecution(String eventType)
    {
        if ("com.armedia.acm.app.task.create".equals(eventType) ||
                "com.armedia.acm.app.task.save".equals(eventType) ||
                "com.armedia.acm.app.task.delete".equals(eventType) ||
                "com.armedia.acm.app.task.complete".equals(eventType) ||
                "com.armedia.acm.app.task.terminate".equals(eventType))
        {
            return true;
        }

        return false;
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

}
