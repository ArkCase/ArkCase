package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author ivana.shekerova on 04/04/2019.
 */
public class ChangedParticipant
{

    private String cmisObjectId;
    private String changeType;
    private AcmParticipant changedParticipant;

    public String getCmisObjectId()
    {
        return cmisObjectId;
    }

    public void setCmisObjectId(String cmisObjectId)
    {
        this.cmisObjectId = cmisObjectId;
    }

    public String getChangeType()
    {
        return changeType;
    }

    public void setChangeType(String changeType)
    {
        this.changeType = changeType;
    }

    public AcmParticipant getChangedParticipant()
    {
        return changedParticipant;
    }

    public void setChangedParticipant(AcmParticipant changedParticipant)
    {
        this.changedParticipant = changedParticipant;
    }
}
