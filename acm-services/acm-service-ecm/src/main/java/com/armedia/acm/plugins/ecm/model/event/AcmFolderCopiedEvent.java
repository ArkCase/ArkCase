package com.armedia.acm.plugins.ecm.model.event;

import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;

/**
 * Created by marjan.stefanoski on 20.04.2015.
 */
public class AcmFolderCopiedEvent extends AcmFolderPersistenceEvent {

    public AcmFolderCopiedEvent(AcmFolder source, String userId, String ipAddress) {
        super(source, userId, ipAddress);
    }

    @Override
    public String getEventType() {
        return AcmFolderConstants.EVENT_TYPE_FOLDER_COPIED;
    }
}
