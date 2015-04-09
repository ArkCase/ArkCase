package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;

import java.util.Date;

public class AcmFolderPersistenceEvent extends AcmEvent {


    public AcmFolderPersistenceEvent(AcmFolder source, String userId) {

        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(userId);
        setObjectType(AcmFolderConstants.OBJECT_TYPE);
    }

    @Override
    public String getObjectType()
    {
        return AcmFolderConstants.OBJECT_TYPE;
    }
}
