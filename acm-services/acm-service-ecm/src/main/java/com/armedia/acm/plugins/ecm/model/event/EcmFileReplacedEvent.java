package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFileReplacedEvent extends  EcmFilePersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.file.replaced";

    public EcmFileReplacedEvent(EcmFile source, String userId , String ipAddress) {
        super(source,userId,ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
