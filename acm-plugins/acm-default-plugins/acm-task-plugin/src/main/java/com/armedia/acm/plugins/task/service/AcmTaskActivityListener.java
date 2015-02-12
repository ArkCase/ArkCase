package com.armedia.acm.plugins.task.service;

import com.armedia.acm.activiti.AcmTaskActivitiEvent;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.AcmTaskAddedEvent;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class AcmTaskActivityListener implements ApplicationListener<AcmTaskActivitiEvent> {

    private TaskEventPublisher taskEventPublisher;

    @Override
    public void onApplicationEvent(AcmTaskActivitiEvent acmTaskActivitiEvent) {

        if( "com.armedia.acm.activiti.task.create".equals(acmTaskActivitiEvent.getEventType()) ) {

            AcmTask source = (AcmTask)acmTaskActivitiEvent.getSource();
            Long objectId =  acmTaskActivitiEvent.getParentObjectId();
            String objectType = acmTaskActivitiEvent.getParentObjectType();
            String userId = acmTaskActivitiEvent.getUserId();
            publishTaskAddedEvent( source, userId, objectType, objectId, true );
        }
    }

    protected void publishTaskAddedEvent( AcmTask source,String userId, String objectType, Long  objectId,boolean succeeded ) {

        AcmTaskAddedEvent acmTaskAddedEvent = new AcmTaskAddedEvent( source, objectId, objectType );
        acmTaskAddedEvent.setSucceeded(succeeded);
        acmTaskAddedEvent.setUserId(userId);

        getTaskEventPublisher().publishAcmEvent(acmTaskAddedEvent);
    }

    public TaskEventPublisher getTaskEventPublisher() {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher) {
        this.taskEventPublisher = taskEventPublisher;
    }
}
