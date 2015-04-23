package com.armedia.acm.plugins.ecm.model.event;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;

/**
 * Created by marjan.stefanoski on 22.04.2015.
 */
public class EcmFileActiveVersionSetEvent extends EcmFilePersistenceEvent {

    public EcmFileActiveVersionSetEvent(EcmFile source, String userId, String ipAddress) {
        super(source, userId, ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EcmFileConstants.EVENT_TYPE_ACTIVE_VERSION_SET;
    }
}
