package com.armedia.acm.event;

/*-
 * #%L
 * ACM Service: Events
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

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.spring.SpringContextHolder;

import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Listen for ACM events, and trigger any registered event responses.
 */
public class AcmEventResponseLauncher implements ApplicationListener<AcmEvent>
{
    private Logger log = LogManager.getLogger(getClass());

    private SpringContextHolder contextHolder;

    private MuleContextManager muleContextManager;

    @Override
    public void onApplicationEvent(AcmEvent acmEvent)
    {
        String eventName = acmEvent.getEventType();

        Map<String, EventResponse> eventResponses = getContextHolder().getAllBeansOfType(EventResponse.class);

        for (EventResponse response : eventResponses.values())
        {
            if (response.getEventName().equals(eventName) && response.isEnabled())
            {
                if (response.getRespondPredicate() != null && !response.getRespondPredicate().evaluate(acmEvent))
                {
                    log.info("Event response predicate returned false - we will not launch the event response.");
                    continue;
                }
                log.info("Launching event response '{}'...", response.getAction().getActionName());

                Map<String, Object> messageProperties = new HashMap<>();
                messageProperties.put("ACM_USER", acmEvent.getUserId());
                messageProperties.put(acmEvent.getObjectType(), acmEvent.getObjectId());
                messageProperties.put("OBJECT_TYPE", acmEvent.getObjectType());
                messageProperties.put("OBJECT_ID", acmEvent.getObjectId());
                messageProperties.put("EVENT_TYPE", acmEvent.getEventType());
                messageProperties.put("EVENT_DATE", acmEvent.getEventDate());
                if (acmEvent.getIpAddress() != null)
                {
                    messageProperties.put("IP_ADDRESS", acmEvent.getIpAddress());
                }
                messageProperties.put("EVENT_SUCCEEDED", acmEvent.isSucceeded());
                addExtraMessageProperties(acmEvent, messageProperties);
                messageProperties.putAll(response.getParameters());

                try
                {
                    getMuleContextManager().dispatch(response.getAction().getTargetMuleEndpoint(), acmEvent, messageProperties);
                }
                catch (MuleException e)
                {
                    log.error("Could not dispatch Mule event: {}", e.getMessage(), e);
                }

            }
        }
    }

    protected void addExtraMessageProperties(AcmEvent acmEvent, Map<String, Object> messageProperties)
    {
        if (acmEvent.getEventProperties() != null && !acmEvent.getEventProperties().isEmpty())
        {
            acmEvent.getEventProperties().forEach((key, value) -> messageProperties.computeIfAbsent(key, mappingKey -> value));
        }
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
