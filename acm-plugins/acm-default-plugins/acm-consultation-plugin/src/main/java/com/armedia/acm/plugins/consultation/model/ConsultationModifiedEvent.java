package com.armedia.acm.plugins.consultation.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class ConsultationModifiedEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;

    private static final String EVENT_TYPE = "com.armedia.acm.consultation";

    private String eventStatus;

    public ConsultationModifiedEvent(Consultation source)
    {
        super(source);

        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setUserId(source.getModifier());
    }

    @Override
    public String getEventType()
    {
        return String.format("%s.%s", EVENT_TYPE, eventStatus);
    }

    public void setEventStatus(String eventStatus)
    {
        this.eventStatus = eventStatus;
    }
}
