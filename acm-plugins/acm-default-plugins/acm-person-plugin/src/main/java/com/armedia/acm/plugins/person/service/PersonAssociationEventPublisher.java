package com.armedia.acm.plugins.person.service;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociationAddEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationDeletedEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationModifiedEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationPersistenceEvent;
import com.armedia.acm.plugins.person.model.PersonAssociationUpdatedEvent;
import com.armedia.acm.plugins.person.model.PersonModifiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PersonAssociationEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishPersonAssociationEvent(
            PersonAssociation source,
            String ipAddress,
            boolean newPersonAssociation,
            boolean succeeded)
    {
        log.debug("Publishing a person event.");

        PersonAssociationPersistenceEvent personAssociationPersistenceEvent =
                newPersonAssociation ? new PersonAssociationAddEvent(source, source.getParentType(), source.getParentId()) :
                        new PersonAssociationUpdatedEvent(source, source.getParentType(), source.getParentId());
        personAssociationPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(personAssociationPersistenceEvent);
    }

    public void publishPersonAssociationEvent(
            String personAssociationHistory,
            PersonAssociation source,
            boolean succeeded)
    {
        log.debug("Publishing a person event.");
        if (personAssociationHistory == null)
        {
            publishPersonAssociationEvent(source, "created", succeeded);
        } else
        {
            AcmUnmarshaller converter = ObjectConverter.createJSONUnmarshaller();
            PersonAssociation previousSource = converter.unmarshall(personAssociationHistory, PersonAssociation.class);
            String parentType = previousSource.getParentType();
            Long parentId = previousSource.getParentId();
            Person exPerson = previousSource.getPerson();
            Person upPerson = source.getPerson();
            checkForPersonAliasRelatedEvents(exPerson, upPerson, succeeded,
                    previousSource.getParentType(), previousSource.getParentId());
            checkForOrganizationRelatedEvents(exPerson, upPerson, succeeded, parentType, parentId);
            checkForContactRelatedEvents(exPerson, upPerson, succeeded, parentType, parentId);
            checkForAddressRelatedEvents(exPerson, upPerson, succeeded, parentType, parentId);
        }
    }

    private void checkForAddressRelatedEvents(Person existingPerson, Person updatedPerson, boolean succeeded,
                                              String parentType, Long parentId)
    {
        boolean isAddressAddedOrRemoved = false;
        List<PostalAddress> existingAddresses = existingPerson.getAddresses();
        List<PostalAddress> updatedAddresses = updatedPerson.getAddresses();
        Set<Long> updatedIds = updatedAddresses.stream()
                .map(PostalAddress::getId)
                .collect(Collectors.toSet());
        Set<Long> existingIds = existingAddresses.stream()
                .map(PostalAddress::getId)
                .collect(Collectors.toSet());

        if (isObjectAdded(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "postalAddress.added", succeeded, parentType, parentId);
            isAddressAddedOrRemoved = true;
        }
        if (isObjectRemoved(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "postalAddress.deleted", succeeded, parentType, parentId);
            isAddressAddedOrRemoved = true;
        }
        if (!isAddressAddedOrRemoved)
        {
            if (isPostalAddressEdited(existingAddresses, updatedAddresses))
            {
                publishPersonEvent(updatedPerson, "postalAddress.updated", succeeded, parentType, parentId);
            }
        }
    }

    private boolean isPostalAddressEdited(List<PostalAddress> existingAddresses, List<PostalAddress> updatedAddresses)
    {
        List<PostalAddress> sortedExisting = existingAddresses.stream()
                .sorted(Comparator.comparing(PostalAddress::getId))
                .collect(Collectors.toList());

        List<PostalAddress> sortedUpdated = updatedAddresses.stream()
                .sorted(Comparator.comparing(PostalAddress::getId))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedExisting.size())
                .anyMatch(i -> !isPostalAddressChanged(sortedExisting.get(i), sortedUpdated.get(i)));
    }

    private void checkForContactRelatedEvents(Person existingPerson, Person updatedPerson, boolean succeeded,
                                              String parentType, Long parentId)
    {
        boolean isContactAddedOrRemoved = false;
        List<ContactMethod> existingContacts = existingPerson.getContactMethods();
        List<ContactMethod> updatedContacts = updatedPerson.getContactMethods();
        Set<Long> updatedIds = updatedContacts.stream()
                .map(ContactMethod::getId)
                .collect(Collectors.toSet());
        Set<Long> existingIds = existingContacts.stream()
                .map(ContactMethod::getId)
                .collect(Collectors.toSet());

        if (isObjectAdded(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "contactMethod.added", succeeded, parentType, parentId);
            isContactAddedOrRemoved = true;
        }
        if (isObjectRemoved(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "contactMethod.deleted", succeeded, parentType, parentId);
            isContactAddedOrRemoved = true;
        }
        if (!isContactAddedOrRemoved)
        {
            if (isContactMethodEdited(existingContacts, updatedContacts))
            {
                publishPersonEvent(updatedPerson, "contactMethod.updated", succeeded, parentType, parentId);
            }
        }
    }

    private boolean isContactMethodEdited(List<ContactMethod> existingContacts, List<ContactMethod> updatedContacts)
    {
        List<ContactMethod> sortedExisting = existingContacts.stream()
                .sorted(Comparator.comparing(ContactMethod::getId))
                .collect(Collectors.toList());

        List<ContactMethod> sortedUpdated = updatedContacts.stream()
                .sorted(Comparator.comparing(ContactMethod::getId))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedExisting.size())
                .anyMatch(i -> !isContactMethodChanged(sortedExisting.get(i), sortedUpdated.get(i)));
    }

    private void checkForOrganizationRelatedEvents(Person existingPerson, Person updatedPerson, boolean succeeded,
                                                   String parentType, Long parentId)
    {
        boolean isOrganizationAddedOrRemoved = false;
        List<Organization> existingOrganizations = existingPerson.getOrganizations();
        List<Organization> updatedOrganizations = updatedPerson.getOrganizations();
        Set<Long> updatedIds = updatedOrganizations.stream()
                .map(Organization::getOrganizationId)
                .collect(Collectors.toSet());
        Set<Long> existingIds = existingOrganizations.stream()
                .map(Organization::getOrganizationId)
                .collect(Collectors.toSet());

        if (isObjectAdded(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "organization.added", succeeded, parentType, parentId);
            isOrganizationAddedOrRemoved = true;
        }
        if (isObjectRemoved(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "organization.deleted", succeeded, parentType, parentId);
            isOrganizationAddedOrRemoved = true;
        }
        if (!isOrganizationAddedOrRemoved)
        {
            if (isOrganizationEdited(existingOrganizations, updatedOrganizations))
            {
                publishPersonEvent(updatedPerson, "organization.updated", succeeded, parentType, parentId);
            }
        }
    }

    private boolean isOrganizationEdited(List<Organization> existingOrganizations, List<Organization> updatedOrganizations)
    {
        List<Organization> sortedExisting = existingOrganizations.stream()
                .sorted(Comparator.comparing(Organization::getOrganizationId))
                .collect(Collectors.toList());

        List<Organization> sortedUpdated = updatedOrganizations.stream()
                .sorted(Comparator.comparing(Organization::getOrganizationId))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedExisting.size())
                .anyMatch(i -> !isOrganizationChanged(sortedExisting.get(i), sortedUpdated.get(i)));
    }


    private boolean isObjectAdded(Set<Long> existingIds, Set<Long> updatedIds)
    {
        return updatedIds.stream().anyMatch(id -> !existingIds.contains(id));
    }

    private boolean isObjectRemoved(Set<Long> existingIds, Set<Long> updatedIds)
    {
        return existingIds.stream().anyMatch(id -> !updatedIds.contains(id));
    }

    private void checkForPersonAliasRelatedEvents(Person existingPerson, Person updatedPerson, boolean succeeded,
                                                  String parentType, Long parentId)
    {
        boolean isPersonAliasAddedOrRemoved = false;
        List<PersonAlias> existingPersonAliases = existingPerson.getPersonAliases();
        List<PersonAlias> updatedPersonAliases = updatedPerson.getPersonAliases();
        Set<Long> updatedIds = updatedPersonAliases.stream()
                .map(PersonAlias::getId)
                .collect(Collectors.toSet());
        Set<Long> existingIds = existingPersonAliases.stream()
                .map(PersonAlias::getId)
                .collect(Collectors.toSet());

        if (isObjectAdded(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "personAlias.added", succeeded, parentType, parentId);
            isPersonAliasAddedOrRemoved = true;
        }
        if (isObjectRemoved(existingIds, updatedIds))
        {
            publishPersonEvent(updatedPerson, "personAlias.deleted", succeeded, parentType, parentId);
            isPersonAliasAddedOrRemoved = true;
        }
        if (!isPersonAliasAddedOrRemoved)
        {
            if (isPersonAliasEdited(existingPersonAliases, updatedPersonAliases))
            {
                publishPersonEvent(updatedPerson, "personAlias.updated", succeeded, parentType, parentId);
            }
        }
    }

    private boolean isPersonAliasEdited(List<PersonAlias> existingPersonAliases, List<PersonAlias> updatedPersonAliases)
    {
        List<PersonAlias> sortedExisting = existingPersonAliases.stream()
                .sorted(Comparator.comparing(PersonAlias::getId))
                .collect(Collectors.toList());

        List<PersonAlias> sortedUpdated = updatedPersonAliases.stream()
                .sorted(Comparator.comparing(PersonAlias::getId))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedExisting.size())
                .anyMatch(i -> !isPersonAliasChanged(sortedExisting.get(i), sortedUpdated.get(i)));
    }

    public void publishPersonEvent(Person person, String eventAction, boolean succeeded, String parentType, Long parentId)
    {
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        PersonModifiedEvent personEvent = new PersonModifiedEvent(person, ipAddress);
        personEvent.setSucceeded(succeeded);
        personEvent.setEventAction(eventAction);
        personEvent.setParentObjectType(parentType);
        personEvent.setParentObjectId(parentId);
        eventPublisher.publishEvent(personEvent);
    }

    public void publishPersonAssociationEvent(PersonAssociation source, String eventAction, boolean succeeded)
    {
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        PersonAssociationModifiedEvent personAssociationEvent = new PersonAssociationModifiedEvent(source, ipAddress);
        personAssociationEvent.setSucceeded(succeeded);
        personAssociationEvent.setEventAction(eventAction);
        personAssociationEvent.setParentObjectType(source.getParentType());
        personAssociationEvent.setParentObjectId(source.getParentId());
        eventPublisher.publishEvent(personAssociationEvent);
    }

    public void publishPersonAssociationDeletedEvent(PersonAssociation source)
    {
        PersonAssociationDeletedEvent event = new PersonAssociationDeletedEvent(source, source.getParentType(), source.getParentId());
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }

    private boolean isContactMethodChanged(ContactMethod ex, ContactMethod up)
    {
        return Objects.equals(ex.getType(), up.getType())
                && Objects.equals(ex.getValue(), up.getValue());
    }

    private boolean isPersonAliasChanged(PersonAlias ex, PersonAlias up)
    {
        return Objects.equals(ex.getAliasType(), up.getAliasType())
                && Objects.equals(ex.getAliasValue(), up.getAliasValue());
    }

    private boolean isPostalAddressChanged(PostalAddress ex, PostalAddress up)
    {
        return Objects.equals(ex.getType(), up.getType())
                && Objects.equals(ex.getStreetAddress(), up.getStreetAddress())
                && Objects.equals(ex.getCity(), up.getCity())
                && Objects.equals(ex.getCountry(), up.getCountry())
                && Objects.equals(ex.getZip(), up.getZip())
                && Objects.equals(ex.getState(), up.getState())
                && ex.getId().equals(up.getId());
    }

    private boolean isOrganizationChanged(Organization ex, Organization up)
    {
        return Objects.equals(ex.getOrganizationType(), up.getOrganizationType())
                && Objects.equals(ex.getOrganizationValue(), up.getOrganizationValue());
    }
}
