package com.armedia.acm.data;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by armdev on 10/21/14.
 */
public class SessionAccumulatingSessionListener extends SessionEventAdapter implements ApplicationEventPublisherAware
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private ObjectChangesBySessionAccumulator descriptorListener;

    private ApplicationEventPublisher applicationEventPublisher;
    private AcmEntityEventPublisher acmEntityEventPublisher;

    @Override
    public void postAcquireClientSession(SessionEvent event)
    {
        super.postAcquireClientSession(event);
        String name = UUID.randomUUID().toString();
        log.trace("acquiring session: set session name to: {}", name);

        event.getSession().setName(name);
        descriptorListener.getChangesBySession().put(name, new AcmObjectChangelist());
        descriptorListener.getEntityChangesBySession().put(name, new CopyOnWriteArrayList<>());
    }

    @Override
    public void postReleaseClientSession(SessionEvent event)
    {
        super.postReleaseClientSession(event);
        log.trace("releasing session: {}", event.getSession().getName());

        descriptorListener.getChangesBySession().remove(event.getSession().getName());
        descriptorListener.getEntityChangesBySession().remove(event.getSession().getName());
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

        Map<String, List<AcmEntityChangesHolder>> entityChangesBySession = getDescriptorListener().getEntityChangesBySession();
        log.trace("Raising entity change events: {}", entityChangesBySession.size());
        entityChangesBySession.get(sessionName).forEach(entityChangesHolder ->
                getAcmEntityEventPublisher().publishEntityChangedEvent(entityChangesHolder));
    }

    @Override
    public void postRollbackTransaction(SessionEvent event)
    {
        super.postRollbackTransaction(event);
        log.trace("Rollback: {}", event.getSession().getName());
        log.debug("Not raising database event: {}",
                getDescriptorListener().getChangesBySession().get(event.getSession().getName()));
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

    public AcmEntityEventPublisher getAcmEntityEventPublisher()
    {
        return acmEntityEventPublisher;
    }

    public void setAcmEntityEventPublisher(AcmEntityEventPublisher acmEntityEventPublisher)
    {
        this.acmEntityEventPublisher = acmEntityEventPublisher;
    }
}
