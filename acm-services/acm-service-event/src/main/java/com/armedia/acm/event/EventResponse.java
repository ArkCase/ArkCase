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

import org.apache.commons.collections.Predicate;

import java.util.Map;

/**
 * A handler for an ACM event. NOTE, all events are automatically audited; there is no special audit EventResponse.
 * Plugins configure a list of zero-to-several handlers to specify actions to be taken in reponse to certain events.
 * When an event is raised, an event manager finds all the registered handlers for that event, and routes the event
 * object to the Mule endpoint specified by the action. The parameters specified in this EventResponse object
 * become inbound properties in the Mule flow.
 */
public class EventResponse
{
    private String eventName;
    private AcmAction action;
    private Map<String, Object> parameters;
    private boolean enabled;
    private Predicate respondPredicate;

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }

    public AcmAction getAction()
    {
        return action;
    }

    public void setAction(AcmAction action)
    {
        this.action = action;
    }

    public Map<String, Object> getParameters()
    {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters)
    {
        this.parameters = parameters;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public Predicate getRespondPredicate()
    {
        return respondPredicate;
    }

    public void setRespondPredicate(Predicate respondPredicate)
    {
        this.respondPredicate = respondPredicate;
    }
}
