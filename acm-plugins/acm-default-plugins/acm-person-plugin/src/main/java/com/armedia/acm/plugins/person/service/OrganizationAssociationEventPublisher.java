package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.OrganizationAssociationAddEvent;
import com.armedia.acm.plugins.person.model.OrganizationAssociationDeletedEvent;
import com.armedia.acm.plugins.person.model.OrganizationAssociationModifiedEvent;
import com.armedia.acm.plugins.person.model.OrganizationAssociationPersistenceEvent;
import com.armedia.acm.plugins.person.model.OrganizationAssociationUpdatedEvent;
import com.armedia.acm.plugins.person.model.OrganizationEvent;
import com.armedia.acm.plugins.person.model.Person;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrganizationAssociationEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;
    private ObjectConverter objectConverter;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    @Async
    public void publishOrganizationAssociationEvent(OrganizationAssociation source, String ipAddress, boolean newOrganizationAssociation,
            boolean succeeded)
    {
        log.debug("Publishing a organization event.");

        OrganizationAssociationPersistenceEvent organizationAssociationPersistenceEvent = newOrganizationAssociation
                ? new OrganizationAssociationAddEvent(source, source.getParentType(), source.getParentId(),
                        AuthenticationUtils.getUserIpAddress())
                : new OrganizationAssociationUpdatedEvent(source, source.getParentType(), source.getParentId(),
                        AuthenticationUtils.getUserIpAddress());
        organizationAssociationPersistenceEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(organizationAssociationPersistenceEvent);
    }

    @Async
    public void publishOrganizationAssociationEvent(String organizationAssociationHistory, OrganizationAssociation source,
            boolean succeeded)
    {
        log.debug("Publishing a organization event.");
        if (organizationAssociationHistory == null)
        {
            publishOrganizationAssociationEvent(source, "created", succeeded);
        }
        else
        {
            AcmUnmarshaller converter = getObjectConverter().getJsonUnmarshaller();
            OrganizationAssociation previousSource = converter.unmarshall(organizationAssociationHistory, OrganizationAssociation.class);
            String parentType = previousSource.getParentType();
            Long parentId = previousSource.getParentId();
            Organization exOrganization = previousSource.getOrganization();
            Organization upOrganization = source.getOrganization();

            checkForContactRelatedEvents(exOrganization, upOrganization, succeeded, parentType, parentId);
            checkForAddressRelatedEvents(exOrganization, upOrganization, succeeded, parentType, parentId);
        }
    }

    private void checkForAddressRelatedEvents(Organization existingOrganization, Organization updatedOrganization, boolean succeeded,
            String parentType, Long parentId)
    {
        boolean isAddressAddedOrRemoved = false;
        List<PostalAddress> existingAddresses = existingOrganization.getAddresses();
        List<PostalAddress> updatedAddresses = updatedOrganization.getAddresses();
        Set<Long> updatedIds = updatedAddresses.stream().map(PostalAddress::getId).collect(Collectors.toSet());
        Set<Long> existingIds = existingAddresses.stream().map(PostalAddress::getId).collect(Collectors.toSet());

        if (isObjectAdded(existingIds, updatedIds))
        {
            publishOrganizationEvent(updatedOrganization, "postalAddress.added", succeeded, parentType, parentId);
            isAddressAddedOrRemoved = true;
        }
        if (isObjectRemoved(existingIds, updatedIds))
        {
            publishOrganizationEvent(updatedOrganization, "postalAddress.deleted", succeeded, parentType, parentId);
            isAddressAddedOrRemoved = true;
        }
        if (!isAddressAddedOrRemoved)
        {
            if (isPostalAddressEdited(existingAddresses, updatedAddresses))
            {
                publishOrganizationEvent(updatedOrganization, "postalAddress.updated", succeeded, parentType, parentId);
            }
        }
    }

    private boolean isPostalAddressEdited(List<PostalAddress> existingAddresses, List<PostalAddress> updatedAddresses)
    {
        List<PostalAddress> sortedExisting = existingAddresses.stream().sorted(Comparator.comparing(PostalAddress::getId))
                .collect(Collectors.toList());

        List<PostalAddress> sortedUpdated = updatedAddresses.stream().sorted(Comparator.comparing(PostalAddress::getId))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedExisting.size())
                .anyMatch(i -> !isPostalAddressChanged(sortedExisting.get(i), sortedUpdated.get(i)));
    }

    private void checkForContactRelatedEvents(Organization existingOrganization, Organization updatedOrganization, boolean succeeded,
            String parentType, Long parentId)
    {
        boolean isContactAddedOrRemoved = false;
        List<ContactMethod> existingContacts = existingOrganization.getContactMethods();
        List<ContactMethod> updatedContacts = updatedOrganization.getContactMethods();
        Set<Long> updatedIds = updatedContacts.stream().map(ContactMethod::getId).collect(Collectors.toSet());
        Set<Long> existingIds = existingContacts.stream().map(ContactMethod::getId).collect(Collectors.toSet());

        if (isObjectAdded(existingIds, updatedIds))
        {
            publishOrganizationEvent(updatedOrganization, "contactMethod.added", succeeded, parentType, parentId);
            isContactAddedOrRemoved = true;
        }
        if (isObjectRemoved(existingIds, updatedIds))
        {
            publishOrganizationEvent(updatedOrganization, "contactMethod.deleted", succeeded, parentType, parentId);
            isContactAddedOrRemoved = true;
        }
        if (!isContactAddedOrRemoved)
        {
            if (isContactMethodEdited(existingContacts, updatedContacts))
            {
                publishOrganizationEvent(updatedOrganization, "contactMethod.updated", succeeded, parentType, parentId);
            }
        }
    }

    private boolean isContactMethodEdited(List<ContactMethod> existingContacts, List<ContactMethod> updatedContacts)
    {
        List<ContactMethod> sortedExisting = existingContacts.stream().sorted(Comparator.comparing(ContactMethod::getId))
                .collect(Collectors.toList());

        List<ContactMethod> sortedUpdated = updatedContacts.stream().sorted(Comparator.comparing(ContactMethod::getId))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedExisting.size())
                .anyMatch(i -> !isContactMethodChanged(sortedExisting.get(i), sortedUpdated.get(i)));
    }

    private boolean isObjectAdded(Set<Long> existingIds, Set<Long> updatedIds)
    {
        return updatedIds.stream().anyMatch(id -> !existingIds.contains(id));
    }

    private boolean isObjectRemoved(Set<Long> existingIds, Set<Long> updatedIds)
    {
        return existingIds.stream().anyMatch(id -> !updatedIds.contains(id));
    }

    @Async
    public void publishOrganizationEvent(Organization organization, String eventStatus, boolean succeeded, String parentType, Long parentId)
    {
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        OrganizationEvent organizationEvent = new OrganizationEvent(organization);
        organizationEvent.setIpAddress(ipAddress);
        organizationEvent.setSucceeded(succeeded);
        organizationEvent.setEventStatus(eventStatus);
        organizationEvent.setParentObjectType(parentType);
        organizationEvent.setParentObjectId(parentId);
        eventPublisher.publishEvent(organizationEvent);
    }

    @Async
    public void publishOrganizationAssociationEvent(OrganizationAssociation source, String eventAction, boolean succeeded)
    {
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        OrganizationAssociationModifiedEvent organizationAssociationEvent = new OrganizationAssociationModifiedEvent(source, ipAddress);
        organizationAssociationEvent.setSucceeded(succeeded);
        organizationAssociationEvent.setEventAction(eventAction);
        organizationAssociationEvent.setParentObjectType(source.getParentType());
        organizationAssociationEvent.setParentObjectId(source.getParentId());
        organizationAssociationEvent
                .setEventDescription("Organization " + eventAction + " (" + source.getOrganization().getOrganizationValue() + ")");
        eventPublisher.publishEvent(organizationAssociationEvent);
    }

    @Async
    public void publishOrganizationAssociationDeletedEvent(OrganizationAssociation source)
    {
        OrganizationAssociationDeletedEvent event = new OrganizationAssociationDeletedEvent(source, source.getParentType(),
                source.getParentId(), AuthenticationUtils.getUserIpAddress());
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }

    private boolean isContactMethodChanged(ContactMethod ex, ContactMethod up)
    {
        return Objects.equals(ex.getType(), up.getType()) && Objects.equals(ex.getValue(), up.getValue());
    }

    private boolean isPostalAddressChanged(PostalAddress ex, PostalAddress up)
    {
        return Objects.equals(ex.getType(), up.getType()) && Objects.equals(ex.getStreetAddress(), up.getStreetAddress())
                && Objects.equals(ex.getCity(), up.getCity()) && Objects.equals(ex.getCountry(), up.getCountry())
                && Objects.equals(ex.getZip(), up.getZip()) && Objects.equals(ex.getState(), up.getState());
    }

    private boolean isPersonChanged(Person ex, Person up)
    {
        // TODO there are lot more fields to check if changed
        return Objects.equals(ex.getFamilyName(), up.getFamilyName());
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
