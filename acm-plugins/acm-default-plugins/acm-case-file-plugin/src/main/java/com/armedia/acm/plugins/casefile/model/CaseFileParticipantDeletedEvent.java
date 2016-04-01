package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.Date;

public class CaseFileParticipantDeletedEvent extends AcmEvent
{

    private static final long serialVersionUID = 2601901328541042900L;
    private static final String EVENT_TYPE = "com.armedia.acm.casefile.participant.deleted";

    public CaseFileParticipantDeletedEvent(AcmParticipant source)
    {
        super(source);
        setObjectType("PARTICIPANT");
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(source.getModifier());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}