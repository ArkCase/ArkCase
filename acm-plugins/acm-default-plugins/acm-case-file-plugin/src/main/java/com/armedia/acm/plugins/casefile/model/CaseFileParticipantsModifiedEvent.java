package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;

import java.util.Date;

public class CaseFileParticipantsModifiedEvent extends AcmEvent
{

    private static final long serialVersionUID = 2601901328541042900L;
    private static final String EVENT_TYPE = "com.armedia.acm.casefile.participant";
    private String eventStatus;

    public CaseFileParticipantsModifiedEvent(AcmParticipant source)
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