package com.armedia.acm.services.participants.model;


public class AcmParticipantCreatedEvent extends  AcmParticipantPersistenceEvent{

    private static final String EVENT_TYPE = "com.armedia.acm.participant.created";

    public AcmParticipantCreatedEvent(AcmParticipant source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
