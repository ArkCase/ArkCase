package com.armedia.acm.services.participants.model;

public class AcmParticipantDeletedEvent extends AcmParticipantPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.participant.deleted";

    public AcmParticipantDeletedEvent(AcmParticipant source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
