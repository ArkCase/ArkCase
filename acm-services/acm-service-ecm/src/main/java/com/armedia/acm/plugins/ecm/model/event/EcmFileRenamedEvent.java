package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFileRenamedEvent extends  EcmFilePersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.file.renamed";

    public EcmFileRenamedEvent(EcmFile source, String userId) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
