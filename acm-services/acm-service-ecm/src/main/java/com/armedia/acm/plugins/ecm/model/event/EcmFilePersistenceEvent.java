package com.armedia.acm.plugins.ecm.model.event;


import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;


import java.util.Date;

public class EcmFilePersistenceEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "FILE";

    public EcmFilePersistenceEvent(EcmFile source, String userId) {

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
