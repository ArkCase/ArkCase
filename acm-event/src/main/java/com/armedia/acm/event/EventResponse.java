package com.armedia.acm.event;

import java.util.Map;

/**
 * A handler for an ACM event. NOTE, all events are automatically audited; there is no special audit EventResponse.
 * Plugins configure a list of zero-to-several handlers to specify actions to be taken in reponse to certain events.
 * When an event is raised, an event manager finds all the registered handlers for that event, and routes the event
 * object to the Mule endpoint specified by the action.  The parameters specified in this EventResponse object
 * become inbound properties in the Mule flow.
 */
public class EventResponse
{
    private String eventName;
    private AcmAction action;
    private Map<String, Object> parameters;
    private boolean enabled;

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
}
