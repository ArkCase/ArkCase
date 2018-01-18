package com.armedia.acm.plugins.ecm.model.event;

import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFileRenamedEvent extends EcmFilePersistenceEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.file.renamed";

    public EcmFileRenamedEvent(EcmFile source, String userId, String ipAddress)
    {

        super(source, userId, ipAddress);
        setParentObjectId(source.getContainer().getContainerObjectId());
        setParentObjectType(source.getContainer().getContainerObjectType());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
