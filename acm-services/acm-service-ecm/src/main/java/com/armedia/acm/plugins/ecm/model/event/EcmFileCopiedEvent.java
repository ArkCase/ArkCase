package com.armedia.acm.plugins.ecm.model.event;

import com.armedia.acm.plugins.ecm.model.EcmFile;

public class EcmFileCopiedEvent extends EcmFilePersistenceEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.ecm.file.copied";

    private EcmFile original;

    public EcmFileCopiedEvent(EcmFile source, EcmFile original, String userId, String ipAddress)
    {
        super(source, userId, ipAddress);
        setOriginal(original);
        setParentObjectType(source.getContainer().getContainerObjectType());
        setParentObjectId(source.getContainer().getContainerObjectId());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    public EcmFile getOriginal()
    {
        return original;
    }

    public void setOriginal(EcmFile original)
    {
        this.original = original;
    }
}
