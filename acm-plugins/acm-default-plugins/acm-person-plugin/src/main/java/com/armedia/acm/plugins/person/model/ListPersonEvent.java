package com.armedia.acm.plugins.person.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 6/4/14.
 */
public class ListPersonEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.person.byId";
    
    public ListPersonEvent(Person source)
    {
        super(source);

        setEventType("com.armedia.acm.person.findById");
        setObjectId(source.getId());
        setEventDate(new Date());
        setObjectType("PERSON");
    }
    
      @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
