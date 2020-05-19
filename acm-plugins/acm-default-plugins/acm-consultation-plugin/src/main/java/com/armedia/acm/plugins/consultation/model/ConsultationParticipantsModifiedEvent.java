package com.armedia.acm.plugins.consultation.model;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;

import java.util.Date;

public class ConsultationParticipantsModifiedEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.consultation.participant";
    private String eventStatus;

    public ConsultationParticipantsModifiedEvent(AcmParticipant source)
    {
        super(source);
        setObjectType(ParticipantConstants.OBJECT_TYPE);
        setObjectId(source.getId());
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
