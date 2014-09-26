package com.armedia.acm.plugins.person.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 8/19/14.
 */
public abstract class PersonPersistenceEvent extends AcmEvent
{
    private static final String OBJECT_TYPE = "PERSON";
    
    public PersonPersistenceEvent(Person source)
    {
        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(source.getModifier());
               
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

    
}
