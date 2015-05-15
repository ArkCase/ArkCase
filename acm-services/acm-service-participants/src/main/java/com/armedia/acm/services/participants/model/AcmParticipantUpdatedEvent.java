package com.armedia.acm.services.participants.model;


public class AcmParticipantUpdatedEvent extends AcmParticipantPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.participant.updated";

    public AcmParticipantUpdatedEvent(AcmParticipant source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
