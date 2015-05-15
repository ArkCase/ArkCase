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

        try
        {
            getChangesBySession().get(event.getSession().getName()).getAddedObjects().add(event.getObject());
        }
        catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getChangesBySession() == null)
            {
                nullPart = "getChangesBySession";
            }
            else if ( event == null)
            {
                nullPart = "event";
            }
            else if ( event.getSession() == null )
            {
                nullPart = "event.getSession";
            } else if ( event.getSession().getName() == null )
            {
                nullPart = "event.getSession().getName()";
            }
            else if ( getChangesBySession().get(event.getSession().getName()) == null )
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            }
            else if ( event.getObject() == null )
            {
                nullPart = "event.getObject()";
            }
            else if ( getChangesBySession().get(event.getSession().getName()).getAddedObjects() == null )
            {
                nullPart = "getChangesBySession().get(event.getSession().getName()).getAddedObjects()";
            }
            log.error("Mystery Null Pointer Exception: Null part is... " + nullPart);
        }
    }

    @Override
    public void postUpdate(DescriptorEvent event)
    {
        super.postUpdate(event);
        log.trace("After update: " + event.getObject().getClass().getName());

        try
        {
            getChangesBySession().get(event.getSession().getName()).getUpdatedObjects().add(event.getObject());
        }
        catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if ( getChangesBySession() == null )
            {
                nullPart = "getChangesBySession";
            }
            else if ( event == null )
            {
                nullPart = "event";
            }
            else if ( event.getSession() == null )
            {
                nullPart = "event.getSession";
            }
            else if ( event.getSession().getName() == null )
            {
                nullPart = "event.getSession().getName()";
            }
            else if ( getChangesBySession().get(event.getSession().getName()) == null )
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            }
            else if ( event.getObject() == null )
            {
                nullPart = "event.getObject()";
            }
            else if ( getChangesBySession().get(event.getSession().getName()).getUpdatedObjects() == null )
            {
                nullPart = "getChangesBySession().get(event.getSession().getName()).getUpdatedObjects()";
            }
            log.error("Mystery Null Pointer Exception: Null part is... " + nullPart);
        }


    }

    @Override
    public void postDelete(DescriptorEvent event)
    {
        super.postDelete(event);
        log.trace("After delete: " + event.getObject().getClass().getName());

        try
        {
            getChangesBySession().get(event.getSession().getName()).getDeletedObjects().add(event.getObject());
        }
        catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if ( getChangesBySession() == null )
            {
                nullPart = "getChangesBySession";
            }
            else if ( event == null )
            {
                nullPart = "event";
            }
            else if ( event.getSession() == null )
            {
                nullPart = "event.getSession";
            }
            else if ( event.getSession().getName() == null )
            {
                nullPart = "event.getSession().getName()";
            }
            else if ( getChangesBySession().get(event.getSession().getName()) == null )
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            }
            else if ( event.getObject() == null )
            {
                nullPart = "event.getObject()";
            }
            else if ( getChangesBySession().get(event.getSession().getName()).getDeletedObjects() == null )
            {
                nullPart = "getChangesBySession().get(event.getSession().getName()).getDeletedObjects()";
            }
            log.error("Mystery Null Pointer Exception: Null part is... " + nullPart);
        }


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
