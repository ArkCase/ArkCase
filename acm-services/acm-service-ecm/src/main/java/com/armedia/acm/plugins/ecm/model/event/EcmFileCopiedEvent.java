package com.armedia.acm.plugins.ecm.model.event;

import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFileCopiedEvent extends EcmFilePersistenceEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.ecm.file.copied";

    public EcmFileCopiedEvent(EcmFile source, String userId, String ipAddress)
    {
        super(source, userId, ipAddress);
        setParentObjectType(source.getContainer().getContainerObjectType());
        setParentObjectId(source.getContainer().getContainerObjectId());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
