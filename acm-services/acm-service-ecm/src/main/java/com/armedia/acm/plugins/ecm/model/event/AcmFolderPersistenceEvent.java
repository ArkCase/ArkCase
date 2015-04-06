package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.plugins.ecm.model.AcmFolder;

import java.util.Date;

public class AcmFolderPersistenceEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "FOLDER";

    public AcmFolderPersistenceEvent(AcmFolder source, String userId) {

        super(source);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(userId);
        setObjectType(OBJECT_TYPE);
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }
}
