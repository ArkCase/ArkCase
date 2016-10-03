package com.armedia.acm.plugins.person.service;


import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAliasEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class PersonAliasEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishPersonAliasUpdatedEvent(PersonAlias source, boolean succeeded)
    {
        PersonAliasEvent personAliasEvent = new PersonAliasEvent(source, AuthenticationUtils.getUserIpAddress());
        personAliasEvent.setSucceeded(succeeded);
        personAliasEvent.setEventAction(PersonConstants.EVENT_ACTION_UPDATE);
        eventPublisher.publishEvent(personAliasEvent);
    }

    public void publishPersonAliasCreatedEvent(PersonAlias source, boolean succeeded)
    {
        PersonAliasEvent personAliasEvent = new PersonAliasEvent(source, AuthenticationUtils.getUserIpAddress());
        personAliasEvent.setSucceeded(succeeded);
        personAliasEvent.setEventAction(PersonConstants.EVENT_ACTION_INSERT);
        eventPublisher.publishEvent(personAliasEvent);
    }

    public void publishPersonAliasDeletedEvent(String entityId, boolean succeeded)
    {
        PersonAlias personAlias = new PersonAlias();
        personAlias.setId(Long.parseLong(entityId));
        personAlias.setModifier(AuthenticationUtils.getUsername());
        PersonAliasEvent personAliasEvent = new PersonAliasEvent(personAlias, AuthenticationUtils.getUserIpAddress());
        personAliasEvent.setEventAction(PersonConstants.EVENT_ACTION_DELETE);
        personAliasEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(personAliasEvent);
    }
}
