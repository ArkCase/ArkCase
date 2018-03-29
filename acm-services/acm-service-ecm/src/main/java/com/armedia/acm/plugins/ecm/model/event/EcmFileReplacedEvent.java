package com.armedia.acm.plugins.ecm.model.event;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

public class EcmFileReplacedEvent extends EcmFilePersistenceEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.ecm.file.replaced";

    private EcmFileVersion previousActiveFileVersion;

    public EcmFileReplacedEvent(EcmFile source, EcmFileVersion previousActiveFileVersion, String userId, String ipAddress)
    {
        super(source, userId, ipAddress);
        setPreviousActiveFileVersion(previousActiveFileVersion);
        setParentObjectId(source.getContainer().getContainerObjectId());
        setParentObjectType(source.getContainer().getContainerObjectType());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    public EcmFileVersion getPreviousActiveFileVersion()
    {
        return previousActiveFileVersion;
    }

    public void setPreviousActiveFileVersion(EcmFileVersion previousActiveFileVersion)
    {
        this.previousActiveFileVersion = previousActiveFileVersion;
    }
}
