/**
 * 
 */
package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintPersistenceEvent;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

/**
 * @author riste.tutureski
 *
 */
public class ComplaintHistoryListener implements ApplicationListener<ComplaintPersistenceEvent>
{

    private static final String OBJECT_TYPE = "COMPLAINT";
    private final Logger LOG = LogManager.getLogger(getClass());
    private AcmObjectHistoryService acmObjectHistoryService;

    @Override
    public void onApplicationEvent(ComplaintPersistenceEvent event)
    {
        LOG.debug("Complaint event raised. Start adding it to the object history ...");

        if (event != null)
        {
            boolean execute = checkExecution(event.getEventType());

            if (execute)
            {
                Complaint complaint = (Complaint) event.getSource();

                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), complaint, complaint.getComplaintId(),
                        OBJECT_TYPE, event.getEventDate(), event.getIpAddress(), event.isSucceeded());
            }
        }
    }

    private boolean checkExecution(String eventType)
    {
        if ("com.armedia.acm.complaint.created".equals(eventType) ||
                "com.armedia.acm.complaint.updated".equals(eventType))
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
