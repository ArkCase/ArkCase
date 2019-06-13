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

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.UUID;

/**
 * Created by armdev on 10/21/14.
 */
public class SessionAccumulatingSessionListener extends SessionEventAdapter implements ApplicationEventPublisherAware
{
    private Logger log = LogManager.getLogger(getClass());
    private ObjectChangesBySessionAccumulator descriptorListener;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void postAcquireClientSession(SessionEvent event)
    {
        super.postAcquireClientSession(event);
        String name = UUID.randomUUID().toString();
        log.trace("acquiring session: set session name to: {}", name);

        event.getSession().setName(name);
        descriptorListener.getChangesBySession().put(name, new AcmObjectChangelist());
        descriptorListener.getRollbackChangesBySession().put(name, new AcmObjectRollbacklist());
    }

    @Override
    public void postReleaseClientSession(SessionEvent event)
    {
        super.postReleaseClientSession(event);
        log.trace("releasing session: {}", event.getSession().getName());

        descriptorListener.getChangesBySession().remove(event.getSession().getName());
        descriptorListener.getRollbackChangesBySession().remove(event.getSession().getName());
    }

    @Override
    public void postCommitTransaction(SessionEvent event)
    {
        super.postCommitTransaction(event);

        String sessionName = event.getSession().getName();

        log.trace("Session committed: {}", sessionName);
        log.trace("Raising database event: {}",
                getDescriptorListener().getChangesBySession().get(sessionName));

        AcmObjectChangelist changelist = getDescriptorListener().getChangesBySession().get(sessionName);

        if (changelist != null)
        {
            boolean raiseEvent = !changelist.getAddedObjects().isEmpty() || !changelist.getDeletedObjects().isEmpty()
                    || !changelist.getUpdatedObjects().isEmpty();
            if (raiseEvent)
            {
                getApplicationEventPublisher().publishEvent(new AcmDatabaseChangesEvent(changelist));
            }
        }
    }

    @Override
    public void postRollbackTransaction(SessionEvent event)
    {
        super.postRollbackTransaction(event);

        String sessionName = event.getSession().getName();

        log.trace("Rollback: {}", sessionName);
        log.debug("Raising rollback database event: {}",
                getDescriptorListener().getRollbackChangesBySession().get(sessionName));

        AcmObjectRollbacklist rollbackChangelist = getDescriptorListener().getRollbackChangesBySession().get(sessionName);

        if (rollbackChangelist != null)
        {
            boolean raiseEvent = !rollbackChangelist.getPreInsertObjects().isEmpty();
            if (raiseEvent)
            {
                getApplicationEventPublisher().publishEvent(new AcmDatabaseRollbackChangesEvent(rollbackChangelist));
            }
        }
    }

    public ObjectChangesBySessionAccumulator getDescriptorListener()
    {
        return descriptorListener;
    }

    public void setDescriptorListener(ObjectChangesBySessionAccumulator descriptorListener)
    {
        this.descriptorListener = descriptorListener;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
