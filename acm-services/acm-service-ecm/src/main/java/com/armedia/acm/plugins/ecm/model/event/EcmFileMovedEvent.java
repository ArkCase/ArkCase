package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFileMovedEvent extends EcmFilePersistenceEvent{

    private static final String EVENT_TYPE = "com.armedia.acm.file.moved";

    public EcmFileMovedEvent(EcmFile source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
