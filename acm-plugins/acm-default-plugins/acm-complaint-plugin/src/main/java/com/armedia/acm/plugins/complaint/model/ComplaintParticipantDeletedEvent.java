package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;

import java.util.Date;

public class ComplaintParticipantDeletedEvent extends AcmEvent
{

    private static final long serialVersionUID = 2601901328541042900L;
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.participant.deleted";

    public ComplaintParticipantDeletedEvent(AcmParticipant source)
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
        return EVENT_TYPE;
    }
}