package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;

import java.util.Date;

public class AcmFolderPersistenceEvent extends AcmEvent {


    public AcmFolderPersistenceEvent(AcmFolder source, String userId, String ipAddress) {

        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(userId);
        setObjectType(AcmFolderConstants.OBJECT_FOLDER_TYPE);
        setIpAddress(ipAddress);
    }

    @Override
    public String getObjectType()
    {
        return AcmFolderConstants.OBJECT_FOLDER_TYPE;
    }
}
