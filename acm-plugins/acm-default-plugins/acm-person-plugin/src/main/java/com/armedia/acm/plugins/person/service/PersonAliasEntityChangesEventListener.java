package com.armedia.acm.plugins.person.service;


import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.data.AcmEntityChangeEvent;
import com.armedia.acm.data.AcmEntityChangesHolder;
import com.armedia.acm.plugins.person.dao.PersonAliasDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

public class PersonAliasEntityChangesEventListener implements ApplicationListener<AcmEntityChangeEvent>
{
    static final Logger LOG = LoggerFactory.getLogger(PersonAliasEntityChangesEventListener.class);

    private PersonAliasDao personAliasDao;
    private PersonAliasEventPublisher personAliasEventPublisher;
    private PersonEventPublisher personEventPublisher;
    private PersonAssociationEventPublisher personAssociationEventPublisher;

    @Override
    public void onApplicationEvent(AcmEntityChangeEvent event)
    {
        AcmEntityChangesHolder changesHolder = event.getAcmEntityChangesHolder();
        if (changesHolder.getEntityClass().equals(PersonAlias.class.getName()))
        {
            AcmEntityChangeEvent.ACTION action = changesHolder.getEntityChangeAction();
            if (action == AcmEntityChangeEvent.ACTION.UPDATE)
            {
                LOG.debug("Person alias updated: {}", event);
                PersonAlias personAlias = getPersonAliasDao().find(Long.parseLong(changesHolder.getEntityId()));
                getPersonAliasEventPublisher().publishPersonAliasUpdatedEvent(personAlias, true);
                publishPersonAndPersonAssociationsUpdateEvents(personAlias.getPerson());
            } else if (action == AcmEntityChangeEvent.ACTION.INSERT)
            {
                LOG.debug("Person alias created: {}", event);
                PersonAlias personAlias = getPersonAliasDao().find(Long.parseLong(changesHolder.getEntityId()));
                getPersonAliasEventPublisher().publishPersonAliasCreatedEvent(personAlias, true);
                publishPersonAndPersonAssociationsUpdateEvents(personAlias.getPerson());
            } else if (action == AcmEntityChangeEvent.ACTION.DELETE)
            {
                LOG.debug("Person alias deleted: {}", event);
                getPersonAliasEventPublisher().publishPersonAliasDeletedEvent(changesHolder.getEntityId(), true);
                // TODO retrieve person and person association and publish updated event
            }
        }
    }

    public void publishPersonAndPersonAssociationsUpdateEvents(Person person)
    {
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        getPersonEventPublisher().publishPersonEvent(person, ipAddress, false, true);
        List<PersonAssociation> personAssociations = person.getPersonAssociations();
        if (personAssociations != null)
        {
            personAssociations.forEach(pa ->
                    getPersonAssociationEventPublisher().publishPersonAssociationEvent(pa, ipAddress, false, true)
            );
        }
    }

    public PersonAliasDao getPersonAliasDao()
    {
        return personAliasDao;
    }

    public void setPersonAliasDao(PersonAliasDao personAliasDao)
    {
        this.personAliasDao = personAliasDao;
    }

    public PersonEventPublisher getPersonEventPublisher()
    {
        return personEventPublisher;
    }

    public void setPersonEventPublisher(PersonEventPublisher personEventPublisher)
    {
        this.personEventPublisher = personEventPublisher;
    }

    public PersonAssociationEventPublisher getPersonAssociationEventPublisher()
    {
        return personAssociationEventPublisher;
    }

    public void setPersonAssociationEventPublisher(PersonAssociationEventPublisher personAssociationEventPublisher)
    {
        this.personAssociationEventPublisher = personAssociationEventPublisher;
    }

    public PersonAliasEventPublisher getPersonAliasEventPublisher()
    {
        return personAliasEventPublisher;
    }

    public void setPersonAliasEventPublisher(PersonAliasEventPublisher personAliasEventPublisher)
    {
        this.personAliasEventPublisher = personAliasEventPublisher;
    }
}
