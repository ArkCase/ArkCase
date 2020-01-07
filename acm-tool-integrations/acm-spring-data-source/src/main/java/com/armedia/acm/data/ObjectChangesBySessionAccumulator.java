package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ObjectChangesBySessionAccumulator extends DescriptorEventAdapter
{
    private Logger log = LogManager.getLogger(getClass());

    private Map<String, AcmObjectChangelist> changesBySession = Collections.synchronizedMap(new HashMap<>());

    private Map<String, AcmObjectRollbacklist> rollbackChangesBySession = Collections
            .synchronizedMap(new HashMap<String, AcmObjectRollbacklist>());

    @Override
    public void postInsert(DescriptorEvent event)
    {
        super.postInsert(event);
        log.trace("After insert: {}", event.getObject().getClass().getName());

        try
        {
            String sessionName = event.getSession().getName();
            // inherited objects notify listeners for the object and parent object class
            // we filter out these duplicate events.
            // See implementation of {@link DescriptorEventManager#notifyListeners(DescriptorEvent)}
            if (!getChangesBySession().get(sessionName).getAddedObjects().contains(event.getObject()))
            {
                getChangesBySession().get(sessionName).getAddedObjects().add(event.getObject());
            }
        }
        catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getChangesBySession() == null)
            {
                nullPart = "getChangesBySession";
            }
            else if (event.getSession() == null)
            {
                nullPart = "event.getSession";
            }
            else if (event.getSession().getName() == null)
            {
                nullPart = "event.getSession().getName()";
            }
            else if (getChangesBySession().get(event.getSession().getName()) == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            }
            else if (event.getObject() == null)
            {
                nullPart = "event.getObject()";
            }
            else if (getChangesBySession().get(event.getSession().getName()).getAddedObjects() == null)
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
        log.trace("After update: {}", event.getObject().getClass().getName());

        try
        {
            String sessionName = event.getSession().getName();
            // inherited objects notify listeners for the object and parent object class
            // we filter out these duplicate events.
            // See implementation of {@link DescriptorEventManager#notifyListeners(DescriptorEvent)}
            if (!getChangesBySession().get(sessionName).getUpdatedObjects().contains(event.getObject()))
            {
                getChangesBySession().get(sessionName).getUpdatedObjects().add(event.getObject());
            }
        }
        catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getChangesBySession() == null)
            {
                nullPart = "getChangesBySession";
            }
            else if (event.getSession() == null)
            {
                nullPart = "event.getSession";
            }
            else if (event.getSession().getName() == null)
            {
                nullPart = "event.getSession().getName()";
            }
            else if (getChangesBySession().get(event.getSession().getName()) == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            }
            else if (event.getObject() == null)
            {
                nullPart = "event.getObject()";
            }
            else if (getChangesBySession().get(event.getSession().getName()).getUpdatedObjects() == null)
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
        log.trace("After delete: {}", event.getObject().getClass().getName());

        try
        {
            String sessionName = event.getSession().getName();
            // inherited objects notify listeners for the object and parent object class
            // we filter out these duplicate events.
            // See implementation of {@link DescriptorEventManager#notifyListeners(DescriptorEvent)}
            if (!getChangesBySession().get(sessionName).getDeletedObjects().contains(event.getObject()))
            {
                getChangesBySession().get(sessionName).getDeletedObjects().add(event.getObject());
            }
        }
        catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getChangesBySession() == null)
            {
                nullPart = "getChangesBySession";
            }
            else if (event.getSession() == null)
            {
                nullPart = "event.getSession";
            }
            else if (event.getSession().getName() == null)
            {
                nullPart = "event.getSession().getName()";
            }
            else if (getChangesBySession().get(event.getSession().getName()) == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            }
            else if (event.getObject() == null)
            {
                nullPart = "event.getObject()";
            }
            else if (getChangesBySession().get(event.getSession().getName()).getDeletedObjects() == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName()).getDeletedObjects()";
            }
            log.error("Mystery Null Pointer Exception: Null part is... " + nullPart);
        }

    }

    @Override
    public void preInsert(DescriptorEvent event)
    {
        super.preInsert(event);
        log.trace("Before insert: {}", event.getObject().getClass().getName());

        try
        {
            String sessionName = event.getSession().getName();
            // inherited objects notify listeners for the object and parent object class
            // we filter out these duplicate events.
            // See implementation of {@link DescriptorEventManager#notifyListeners(DescriptorEvent)}
            if (!getRollbackChangesBySession().get(sessionName).getPreInsertObjects().contains(event.getObject()))
            {
                getRollbackChangesBySession().get(sessionName).getPreInsertObjects().add(event.getObject());
            }
        }
        catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getRollbackChangesBySession() == null)
            {
                nullPart = "getRollbackChangesBySession";
            }
            else if (event == null)
            {
                nullPart = "event";
            }
            else if (event.getSession() == null)
            {
                nullPart = "event.getSession";
            }
            else if (event.getSession().getName() == null)
            {
                nullPart = "event.getSession().getName()";
            }
            else if (getRollbackChangesBySession().get(event.getSession().getName()) == null)
            {
                nullPart = "getRollbackChangesBySession().get(event.getSession().getName())";
            }
            else if (event.getObject() == null)
            {
                nullPart = "event.getObject()";
            }
            else if (getRollbackChangesBySession().get(event.getSession().getName()).getPreInsertObjects() == null)
            {
                nullPart = "getRollbackChangesBySession().get(event.getSession().getName()).getPreInsertObjects()";
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

    /**
     * @return the rollbackChangesBySession
     */
    public Map<String, AcmObjectRollbacklist> getRollbackChangesBySession()
    {
        return rollbackChangesBySession;
    }

    /**
     * @param rollbackChangesBySession
     *            the rollbackChangesBySession to set
     */
    public void setRollbackChangesBySession(Map<String, AcmObjectRollbacklist> rollbackChangesBySession)
    {
        this.rollbackChangesBySession = rollbackChangesBySession;
    }
}
