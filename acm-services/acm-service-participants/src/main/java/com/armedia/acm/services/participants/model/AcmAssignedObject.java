package com.armedia.acm.services.participants.model;

import com.armedia.acm.core.AcmObject;

import java.util.List;

/**
 * Created by armdev on 1/2/15.
 */
public interface AcmAssignedObject extends AcmObject
{
    List<AcmParticipant> getParticipants();

    String getStatus();
}
