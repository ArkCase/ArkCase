package com.armedia.acm.plugins.ecm.model.event;

import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by bojan.mickoski on 2/29/2016.
 */
public class EcmFileUnlockEvent extends EcmFilePersistenceEvent{

    private static final String EVENT_TYPE = "com.armedia.acm.file.unlock";

    public EcmFileUnlockEvent(EcmFile source, String userId, String ipAddress) {
        super(source, userId, ipAddress);
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
