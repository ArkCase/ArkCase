package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by armdev on 1/6/15.
 */
public class PreObjectChangeAccumulator extends DescriptorEventAdapter implements ApplicationEventPublisherAware
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void preInsert(DescriptorEvent event)
    {
        super.preInsert(event);
        log.trace("Pre insert: " + event.getObject().getClass().getName());

        applicationEventPublisher.publishEvent(new AcmDatabasePreCommitEvent(event.getSource(), "insert"));

    }

    @Override
    public void preUpdate(DescriptorEvent event)
    {
        super.preUpdate(event);
        log.trace("Pre update: " + event.getObject().getClass().getName());

        applicationEventPublisher.publishEvent(new AcmDatabasePreCommitEvent(event.getSource(), "update"));
    }

    @Override
    public void preDelete(DescriptorEvent event)
    {
        super.preDelete(event);
        log.trace("Pre delete: " + event.getObject().getClass().getName());

        applicationEventPublisher.publishEvent(new AcmDatabasePreCommitEvent(event.getSource(), "delete"));
    }

}
