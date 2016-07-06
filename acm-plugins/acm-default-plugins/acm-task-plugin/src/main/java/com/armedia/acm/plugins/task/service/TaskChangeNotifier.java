package com.armedia.acm.plugins.task.service;

import com.armedia.acm.data.AcmObjectChangedNotifier;
import com.armedia.acm.data.AcmObjectEventConstants;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by dmiller on 6/15/16.
 */
public class TaskChangeNotifier implements ApplicationListener<AcmApplicationTaskEvent>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectChangedNotifier acmObjectChangedNotifier;

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent acmApplicationTaskEvent)
    {
        log.debug("event type: {}", acmApplicationTaskEvent.getEventType());
        if (acmApplicationTaskEvent.getEventType().endsWith(".create"))
        {
            getAcmObjectChangedNotifier().notifyChange(AcmObjectEventConstants.ACTION_INSERT, acmApplicationTaskEvent.getAcmTask());
        } else if (acmApplicationTaskEvent.getEventType().endsWith(".delete"))
        {
            getAcmObjectChangedNotifier().notifyChange(AcmObjectEventConstants.ACTION_DELETE, acmApplicationTaskEvent.getAcmTask());
        } else if(acmApplicationTaskEvent.getEventType().endsWith(".changed"))
        {
            getAcmObjectChangedNotifier().notifyChange(AcmObjectEventConstants.ACTION_UPDATE, acmApplicationTaskEvent.getAcmTask());
        }
    }

    public AcmObjectChangedNotifier getAcmObjectChangedNotifier()
    {
        return acmObjectChangedNotifier;
    }

    public void setAcmObjectChangedNotifier(AcmObjectChangedNotifier acmObjectChangedNotifier)
    {
        this.acmObjectChangedNotifier = acmObjectChangedNotifier;
    }
}
