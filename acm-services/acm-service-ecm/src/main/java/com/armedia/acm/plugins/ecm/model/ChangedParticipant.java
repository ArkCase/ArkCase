package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author ivana.shekerova on 04/04/2019.
 */
public class ChangedParticipant
{

    private String cmisObjectId;
    private String changeType;
    private String userDomain;
    private AcmParticipant changedParticipant;
    private AcmParticipant oldParticipant;

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

    public AcmParticipant getOldParticipant()
    {
        return oldParticipant;
    }

    public void setOldParticipant(AcmParticipant oldParticipant)
    {
        this.oldParticipant = oldParticipant;
    }

    public String getUserDomain()
    {
        return userDomain;
    }

    public void setUserDomain(String userDomain)
    {
        this.userDomain = userDomain;
    }
}
