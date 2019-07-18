package com.armedia.acm.services.notification.service.provider.model;

import com.armedia.acm.core.AcmObject;

public class AcmEntityTemplateModel
{
    public AcmObject caseFileObject;
    public String assigneeUserId;
    public String modifierUserId;

    public AcmObject getCaseFileObject()
    {
        return caseFileObject;
    }

    public void setCaseFileObject(AcmObject caseFileObject)
    {
        this.caseFileObject = caseFileObject;
    }

    public String getAssigneeUserId()
    {
        return assigneeUserId;
    }

    public void setAssigneeUserId(String assigneeUserId)
    {
        this.assigneeUserId = assigneeUserId;
    }

    public String getModifierUserId()
    {
        return modifierUserId;
    }

    public void setModifierUserId(String modifierUserId)
    {
        this.modifierUserId = modifierUserId;
    }
}

