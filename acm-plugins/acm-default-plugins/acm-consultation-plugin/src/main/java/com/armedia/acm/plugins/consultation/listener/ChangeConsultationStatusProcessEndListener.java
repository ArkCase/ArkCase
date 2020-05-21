package com.armedia.acm.plugins.consultation.listener;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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

import com.armedia.acm.activiti.AcmBusinessProcessEvent;
import com.armedia.acm.plugins.consultation.service.ChangeConsultationStateService;
import com.armedia.acm.plugins.task.model.BuckslipProcessStateEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ChangeConsultationStatusProcessEndListener
        implements ApplicationListener<AcmBusinessProcessEvent>, ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ChangeConsultationStateService changeConsultationStateService;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(AcmBusinessProcessEvent event)
    {
        boolean isChangeConsultationStatusWorkflow = checkChangeConsultationStatusWorkflow(event);

        if (isChangeConsultationStatusWorkflow)
        {
            try
            {
                Long consultationId = (Long) event.getProcessVariables().get("CONSULTATION");
                Long requestId = (Long) event.getProcessVariables().get("REQUEST_ID");
                String user = event.getUserId();

                log.debug("Request: [{}]", requestId);
                log.debug("Consultation id: [{}]", consultationId);
                log.debug("User: [{}]", user);

                getChangeConsultationStateService().handleChangeConsultationStatusApproved(consultationId, requestId, user,
                        event.getEventDate(),
                        event.getIpAddress());
            }
            catch (Exception e)
            {
                log.error("Exception handling completed change consultation status: " + e.getMessage(), e);
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

    private boolean checkChangeConsultationStatusWorkflow(AcmBusinessProcessEvent event)
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

        if (!"CHANGE_CONSULTATION_STATUS".equals(pvars.get("REQUEST_TYPE")))
        {
            log.debug("Request type is not CHANGE_CONSULTATION_STATUS: [{}]", pvars.get("REQUEST_TYPE"));
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

        log.debug("This event marks the end of an approved change consultation status.");

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

    public ChangeConsultationStateService getChangeConsultationStateService()
    {
        return changeConsultationStateService;
    }

    public void setChangeConsultationStateService(ChangeConsultationStateService changeConsultationStateService)
    {
        this.changeConsultationStateService = changeConsultationStateService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
