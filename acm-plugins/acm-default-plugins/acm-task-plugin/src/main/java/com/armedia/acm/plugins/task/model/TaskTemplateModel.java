package com.armedia.acm.plugins.task.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;

public class TaskTemplateModel
{
    private AcmObject acmObject;
    private AcmTask task;

    public AcmObject getAcmObject()
    {
        return acmObject;
    }

    public void setAcmObject(AcmObject acmObject)
    {
        this.acmObject = acmObject;
    }

    public AcmTask getTask()
    {
        return task;
    }

    public void setTask(AcmTask task)
    {
        this.task = task;
    }
}
