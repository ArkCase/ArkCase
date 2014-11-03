package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by armdev on 10/30/14.
 */
public class AuditPropertyEntityAdapter extends DescriptorEventAdapter
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ThreadLocal<String> userId = new ThreadLocal<>();

    @Override
    public void preInsert(DescriptorEvent event)
    {
        super.preInsert(event);
        Object data = event.getObject();

        if ( data instanceof AcmEntity )
        {
            log.trace("Entity type '" + data.getClass() + "' is an AcmEntity, setting insert fields.");
            AcmEntity entity = (AcmEntity) data;

            Date today = new Date();
            entity.setCreated(today);
            entity.setModified(today);

            String user = getUserId();
            entity.setCreator(user);
            entity.setModifier(user);
        }
        else
        {
            log.trace("Entity type '" + data.getClass() + "' is NOT an AcmEntity, NOT setting insert fields.");
        }
    }

    @Override
    public void preUpdate(DescriptorEvent event)
    {
        super.preUpdate(event);

        Object data = event.getObject();

        if ( data instanceof AcmEntity )
        {
            log.trace("Entity type '" + data.getClass() + "' is an AcmEntity, setting update fields.");
            AcmEntity entity = (AcmEntity) data;

            Date today = new Date();
            entity.setModified(today);

            String user = getUserId();
            entity.setModifier(user);
        }
        else
        {
            log.trace("Entity type '" + data.getClass() + "' is NOT an AcmEntity, NOT setting update fields.");
        }
    }

    public String getUserId()
    {
        return userId.get();
    }

    public void setUserId(String userId)
    {
        this.userId.set(userId);
    }
}
