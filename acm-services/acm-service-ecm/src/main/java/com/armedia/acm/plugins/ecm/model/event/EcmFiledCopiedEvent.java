package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFiledCopiedEvent extends  EcmFilePersistenceEvent{

    private static final String EVENT_TYPE = "com.armedia.acm.file.copied";

    public EcmFiledCopiedEvent(EcmFile source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
