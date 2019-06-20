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

import com.armedia.acm.activiti.AcmBusinessProcessEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * Created by armdev on 11/13/14.
 */
public class CloseComplaintRequestProcessEndHandler implements ApplicationListener<AcmBusinessProcessEvent>
{
    private final Logger log = LogManager.getLogger(getClass());

    private CloseComplaintRequestService closeComplaintRequestService;

    @Override
    public void onApplicationEvent(AcmBusinessProcessEvent acmBusinessProcessEvent)
    {
        boolean isCloseComplaintWorkflow = checkForCloseComplaintProcess(acmBusinessProcessEvent);

        log.debug("Continue to close complaint processing? " + isCloseComplaintWorkflow);

        if (isCloseComplaintWorkflow)
        {
            try
            {
                Long complaintId = (Long) acmBusinessProcessEvent.getProcessVariables().get("COMPLAINT");
                Long requestId = (Long) acmBusinessProcessEvent.getProcessVariables().get("REQUEST_ID");
                String user = acmBusinessProcessEvent.getUserId();

                log.debug("Request: [{}]", requestId);
                log.debug("Complaint: [{}]", complaintId);
                log.debug("User: [{}]", user);

                getCloseComplaintRequestService().handleCloseComplaintRequestApproved(complaintId, requestId, user,
                        acmBusinessProcessEvent.getEventDate(), acmBusinessProcessEvent.getIpAddress());
            }
            catch (Exception e)
            {
                // we want to log an exception here so we can see what went wrong. And we can't throw an
                // exception from an event handler; plus, we want the exception to propagate, which will roll
                // back all Activiti updates. So we catch the exception, log it, and throw a runtime exception.
                log.error("Exception handling completed close case request: " + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    private boolean checkForCloseComplaintProcess(AcmBusinessProcessEvent acmBusinessProcessEvent)
    {
        if (!"com.armedia.acm.activiti.businessProcess.end".equals(acmBusinessProcessEvent.getEventType()))
        {
            log.debug("event is not the end of a business process: [{}]", acmBusinessProcessEvent.getEventType());
            return false;
        }

        Map<String, Object> pvars = acmBusinessProcessEvent.getProcessVariables();

        if (!pvars.containsKey("REQUEST_TYPE"))
        {
            log.debug("event does not contain a request type");
            return false;
        }

        if (!"CLOSE_COMPLAINT_REQUEST".equals(pvars.get("REQUEST_TYPE")))
        {
            log.debug("request type is not CLOSE_COMPLAINT_REQUEST: [{}]", pvars.get("REQUEST_TYPE"));
            return false;
        }

        if (!pvars.containsKey("reviewOutcome"))
        {
            log.debug("event does not contain a review outcome");
            return false;
        }

        if (!"APPROVE".equals(pvars.get("reviewOutcome")))
        {
            log.debug("review outcome is not APPROVE: [{}]", pvars.get("reviewOutcome"));
            return false;
        }

        log.debug("This event marks the end of an approved close complaint request.");
        return true;
    }

    public CloseComplaintRequestService getCloseComplaintRequestService()
    {
        return closeComplaintRequestService;
    }

    public void setCloseComplaintRequestService(CloseComplaintRequestService closeComplaintRequestService)
    {
        this.closeComplaintRequestService = closeComplaintRequestService;
    }
}
