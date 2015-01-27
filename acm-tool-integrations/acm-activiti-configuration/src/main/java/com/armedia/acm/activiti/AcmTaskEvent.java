package com.armedia.acm.activiti;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by armdev on 6/25/14.
 */
public interface AcmTaskEvent extends Serializable
{
    String getAssignee();

    String getTaskName();

    Date getTaskCreated();

    String getDescription();

    Date getDueDate();

    String getTaskEvent();

    Long getObjectId();
    
    String getPriority();
    
    Long getParentObjectId();
    
    String getParentObjectType();

    String getParentObjectName();

    boolean isAdhocTask();

    String getOwner();

    String getBusinessProcessName();

}
