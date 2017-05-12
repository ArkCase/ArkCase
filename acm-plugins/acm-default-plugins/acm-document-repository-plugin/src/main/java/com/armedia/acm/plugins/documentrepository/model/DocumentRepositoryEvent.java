package com.armedia.acm.plugins.documentrepository.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class DocumentRepositoryEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;

    private static final String EVENT_TYPE = "com.armedia.acm.documentrepository";

    private String eventStatus;

    public DocumentRepositoryEvent(DocumentRepository source)
    {

        super(source);

        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setUserId(source.getModifier());
    }


    public DocumentRepositoryEvent(DocumentRepository source, String eventStatus)
    {

        super(source);

        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setUserId(source.getModifier());
        this.eventStatus = eventStatus;
    }

    @Override
    public String getEventType()
    {
        return String.format("%s.%s", EVENT_TYPE, eventStatus);
    }

    public String getEventStatus()
    {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus)
    {
        this.eventStatus = eventStatus;
    }
}
