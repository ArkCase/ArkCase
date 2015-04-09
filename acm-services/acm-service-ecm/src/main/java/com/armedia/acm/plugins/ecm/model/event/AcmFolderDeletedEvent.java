package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.plugins.ecm.model.AcmFolder;

public class AcmFolderDeletedEvent extends  AcmFolderPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.folder.deleted";

    public AcmFolderDeletedEvent(AcmFolder source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
