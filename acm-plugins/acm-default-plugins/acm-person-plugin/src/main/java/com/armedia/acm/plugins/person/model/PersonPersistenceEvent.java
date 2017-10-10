package com.armedia.acm.plugins.person.model;

import com.armedia.acm.core.model.AcmEvent;

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
        //if modifier is null, event is for creating new person
        setUserId(source.getModifier() != null ? source.getModifier() : source.getCreator());
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }


}
