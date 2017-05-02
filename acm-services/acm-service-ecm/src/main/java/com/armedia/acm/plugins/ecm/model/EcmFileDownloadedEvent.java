package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 6/9/14.
 */
public class EcmFileDownloadedEvent extends AcmEvent
{
    public EcmFileDownloadedEvent(EcmFile source)
    {
        super(source);

        setEventType("com.armedia.acm.ecm.file.downloaded");
        setObjectType("FILE");
        setObjectId(source.getFileId());
        setEventDate(new Date());
        setParentObjectType(source.getContainer().getContainerObjectType());
        setParentObjectId(source.getContainer().getContainerObjectId());
    }
}
