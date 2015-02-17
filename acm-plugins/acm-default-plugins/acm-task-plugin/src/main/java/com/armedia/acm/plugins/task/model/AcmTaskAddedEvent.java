package com.armedia.acm.plugins.task.model;

import com.armedia.acm.activiti.AcmTaskEvent;
import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class AcmTaskAddedEvent extends AcmEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.task.added";

    public AcmTaskAddedEvent( AcmTask source, Long objectId, String objectType) {
        super(source);
        setObjectId(objectId);
        setObjectType(objectType);
    }

    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
