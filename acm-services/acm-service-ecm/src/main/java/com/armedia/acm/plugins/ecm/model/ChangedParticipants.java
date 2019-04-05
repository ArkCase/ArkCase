package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author ivana.shekerova on 4/4/2019.
 */
public class ChangedParticipants {

    private String cmisObjectId;
    private AcmParticipant changedParticipant;

    public String getCmisObjectId() {
        return cmisObjectId;
    }

    public void setCmisObjectId(String cmisObjectId) {
        this.cmisObjectId = cmisObjectId;
    }

    public AcmParticipant getChangedParticipant() {
        return changedParticipant;
    }

    public void setChangedParticipant(AcmParticipant changedParticipant) {
        this.changedParticipant = changedParticipant;
    }
}
