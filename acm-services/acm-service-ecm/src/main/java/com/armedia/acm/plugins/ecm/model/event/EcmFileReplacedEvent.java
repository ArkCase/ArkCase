package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFileReplacedEvent extends  EcmFilePersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.file.replaced";

    public EcmFileReplacedEvent(EcmFile source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
