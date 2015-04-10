package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.plugins.ecm.model.AcmFolder;

public class AcmFolderCreatedEvent extends  AcmFolderPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.folder.created";

    public AcmFolderCreatedEvent(AcmFolder source, String userId, String ipAddress) {
        super(source, userId, ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
