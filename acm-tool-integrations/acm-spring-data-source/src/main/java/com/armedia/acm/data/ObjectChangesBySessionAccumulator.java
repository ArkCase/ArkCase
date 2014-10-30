package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by armdev on 10/21/14.
 */
public class ObjectChangesBySessionAccumulator extends DescriptorEventAdapter
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, AcmObjectChangelist> changesBySession =
            Collections.synchronizedMap(new HashMap<String, AcmObjectChangelist>());

    @Override
    public void postInsert(DescriptorEvent event)
    {
        super.postInsert(event);
        log.trace("After insert: " + event.getObject().getClass().getName());

        getChangesBySession().get(event.getSession().getName()).getAddedObjects().add(event.getObject());
    }

    @Override
    public void postUpdate(DescriptorEvent event)
    {
        super.postUpdate(event);
        log.trace("After update: " + event.getObject().getClass().getName());

        getChangesBySession().get(event.getSession().getName()).getUpdatedObjects().add(event.getObject());
    }

    @Override
    public void postDelete(DescriptorEvent event)
    {
        super.postDelete(event);
        log.trace("After delete: " + event.getObject().getClass().getName());

        getChangesBySession().get(event.getSession().getName()).getDeletedObjects().add(event.getObject());
    }

    public Map<String, AcmObjectChangelist> getChangesBySession()
    {
        return changesBySession;
    }

    public void setChangesBySession(Map<String, AcmObjectChangelist> changesBySession)
    {
        this.changesBySession = changesBySession;
    }
}
