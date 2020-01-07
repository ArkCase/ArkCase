/**
 * 
 */
package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.plugins.task.model.BuckslipProcessStateEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusProcessEndListener implements ApplicationListener<AcmBusinessProcessEvent>, ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ChangeCaseFileStateService changeCaseFileStateService;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(AcmBusinessProcessEvent event)
    {
        boolean isChangeCaseStatusWorkflow = checkChangeCaseStatusWorkflow(event);

        if (isChangeCaseStatusWorkflow)
        {
            try
            {
                Long caseId = (Long) event.getProcessVariables().get("CASE_FILE");
                Long requestId = (Long) event.getProcessVariables().get("REQUEST_ID");
                String user = event.getUserId();

                log.debug("Request: [{}]", requestId);
                log.debug("Case file id: [{}]", caseId);
                log.debug("User: [{}]", user);

                getChangeCaseFileStateService().handleChangeCaseStatusApproved(caseId, requestId, user, event.getEventDate(),
                        event.getIpAddress());
            }
            catch (Exception e)
            {
                log.error("Exception handling completed change case status: " + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        if (isBuckslipWorkflow(event))
        {
            BuckslipProcessStateEvent buckslipProcessStateEvent = new BuckslipProcessStateEvent(event.getProcessVariables());
            buckslipProcessStateEvent.setBuckslipProcessState(BuckslipProcessStateEvent.BuckslipProcessState.COMPLETED);
            applicationEventPublisher.publishEvent(buckslipProcessStateEvent);
        }
    }

    private boolean checkChangeCaseStatusWorkflow(AcmBusinessProcessEvent event)
    {
        if (!"com.armedia.acm.activiti.businessProcess.end".equals(event.getEventType()))
        {
            log.debug("Event is not the end of a business process: [{}]", event.getEventType());
            return false;
        }

        Map<String, Object> pvars = event.getProcessVariables();

        if (!pvars.containsKey("REQUEST_TYPE"))
        {
            log.debug("Event does not contain a request type");
            return false;
        }

        if (!"CHANGE_CASE_STATUS".equals(pvars.get("REQUEST_TYPE")))
        {
            log.debug("Request type is not CHANGE_CASE_STATUS: [{}]", pvars.get("REQUEST_TYPE"));
            return false;
        }

        if (!pvars.containsKey("reviewOutcome"))
        {
            log.debug("Event does not contain a review outcome");
            return false;
        }

        if (!"APPROVE".equals(pvars.get("reviewOutcome")))
        {
            log.debug("Request type is not APPROVE: [{}]", pvars.get("reviewOutcome"));
            return false;
        }

        log.debug("This event marks the end of an approved change case status.");

        return true;
    }

    private boolean isBuckslipWorkflow(AcmBusinessProcessEvent event)
    {
        if (!"com.armedia.acm.activiti.businessProcess.end".equals(event.getEventType()))
        {
            log.debug("Event is not the end of a business process: [{}]", event.getEventType());
            return false;
        }

        Map<String, Object> pvars = event.getProcessVariables();

        if (!pvars.containsKey("buckslipOutcome"))
        {
            log.debug("Event does not contain a buckslip outcome property");
            return false;
        }

        if (!pvars.containsKey("isBuckslipWorkflow"))
        {
            log.debug("Event does not contain a is buckslip workflow property");
            return false;
        }

        if ((boolean) pvars.get("isBuckslipWorkflow") == false)
        {
            log.debug("Process is not buckslip");
            return false;
        }
        return true;
    }

    public ChangeCaseFileStateService getChangeCaseFileStateService()
    {
        return changeCaseFileStateService;
    }

    public void setChangeCaseFileStateService(ChangeCaseFileStateService changeCaseFileStateService)
    {
        this.changeCaseFileStateService = changeCaseFileStateService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
