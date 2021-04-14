package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class CaseFileDueDateUpdatedEvent extends AcmEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.casefile.dueDateChanged";

    public CaseFileDueDateUpdatedEvent(CaseFile source)
    {
        super(source);

        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setUserId(source.getModifier());
        setEventType(EVENT_TYPE);
    }

}
