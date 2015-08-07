package com.armedia.acm.services.participants.model;


import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class AcmParticipantPersistenceEvent extends AcmEvent
{

    private static final String OBJECT_TYPE = "PARTICIPANT";

    public AcmParticipantPersistenceEvent(AcmParticipant source, String userId) {

        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(userId);
        setObjectType(OBJECT_TYPE);
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }
}
