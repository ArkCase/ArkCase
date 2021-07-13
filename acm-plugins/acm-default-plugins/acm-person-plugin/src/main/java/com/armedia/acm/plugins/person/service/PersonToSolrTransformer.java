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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ACM_PARTICIPANTS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CONTACT_METHOD_SS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.FIRST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.LAST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ORGANIZATION_ID_SS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PERSON_TITLE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.POSTAL_ADDRESS_ID_SS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 10/21/14.
 */
public class PersonToSolrTransformer implements AcmObjectToSolrDocTransformer<Person>
{
    private final Logger log = LogManager.getLogger(getClass());
    private PersonDao personDao;
    private UserDao userDao;
    private SearchAccessControlFields searchAccessControlFields;

    @Override
    public List<Person> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Person in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        log.debug("Creating Solr advanced search document for PERSON.");

        String name = in.getGivenName() + " " + in.getFamilyName();
        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), name);

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(Person in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(PERSON_TITLE_LCS, in.getTitle());
        additionalProperties.put(FIRST_NAME_LCS, in.getGivenName());
        additionalProperties.put(LAST_NAME_LCS, in.getFamilyName());
        additionalProperties.put(FULL_NAME_LCS, in.getGivenName() + " " + in.getFamilyName());
        additionalProperties.put(TITLE_PARSEABLE, in.getFamilyName() + " " + in.getGivenName());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getFamilyName() + " " + in.getGivenName());
        additionalProperties.put(STATUS_LCS, in.getStatus());

        addContactMethods(in, additionalProperties);

        addOrganizations(in, additionalProperties);

        addAddresses(in, additionalProperties);

        addAliases(in, additionalProperties);

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, modifier.getFirstName() + " " + modifier.getLastName());
        }

        additionalProperties.put("default_organization_s",
                in.getDefaultOrganization() != null ? in.getDefaultOrganization().getOrganization().getOrganizationValue() : null);
        additionalProperties.put("default_phone_s", getDefaultPhone(in));
        additionalProperties.put("default_location_s", getDefaultAddress(in));
        additionalProperties.put("default_email_lcs", getDefaultEmail(in));

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        additionalProperties.put(ACM_PARTICIPANTS_LCS, participantsListJson);
    }

    private String getDefaultEmail(Person person)
    {
        if (person.getDefaultEmail() != null)
        {
            return person.getDefaultEmail().getValue();
        }
        else
        {
            return person.getContactMethods().stream()
                    .filter(cm -> cm.getType().equalsIgnoreCase("email"))
                    .findFirst()
                    .map(ContactMethod::getValue)
                    .orElse(null);
        }
    }

    private String getDefaultPhone(Person person)
    {
        if (person.getDefaultPhone() == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(person.getDefaultPhone().getValue());
        if (person.getDefaultPhone().getSubType() != null)
        {
            sb.append(" [").append(person.getDefaultPhone().getSubType()).append("]");
        }
        return sb.toString();
    }

    private String getDefaultAddress(Person person)
    {
        if (person.getDefaultAddress() == null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (person.getDefaultAddress().getCity() != null)
        {
            sb.append(person.getDefaultAddress().getCity());
        }
        if (person.getDefaultAddress().getState() != null)
        {
            if (sb.length() > 0)
            {
                sb.append(", ");
            }
            sb.append(person.getDefaultAddress().getState());
        }
        return sb.toString();
    }

    private void addAddresses(Person person, Map<String, Object> additionalProperties)
    {
        List<String> addressIds = new ArrayList<>();
        if (person.getAddresses() != null)
        {
            for (PostalAddress address : person.getAddresses())
            {
                addressIds.add(address.getId() + "-LOCATION");
            }

        }
        additionalProperties.put(POSTAL_ADDRESS_ID_SS, addressIds);
    }

    private void addOrganizations(Person person, Map<String, Object> additionalProperties)
    {
        List<String> organizationIds = new ArrayList<>();
        if (person.getOrganizations() != null)
        {
            for (Organization org : person.getOrganizations())
            {
                organizationIds.add(org.getOrganizationId() + "-ORGANIZATION");
            }
        }
        additionalProperties.put(ORGANIZATION_ID_SS, organizationIds);
    }

    private void addContactMethods(Person person, Map<String, Object> additionalProperties)
    {
        List<String> contactMethodIds = new ArrayList<>();
        if (person.getContactMethods() != null)
        {
            for (ContactMethod cm : person.getContactMethods())
            {
                contactMethodIds.add(cm.getId() + "-CONTACT-METHOD");
            }
        }
        additionalProperties.put(CONTACT_METHOD_SS, contactMethodIds);
    }

    private void addAliases(Person person, Map<String, Object> additionalProperties)
    {
        List<String> aliasIds = new ArrayList<>();
        if (person.getPersonAliases() != null)
        {
            for (PersonAlias pa : person.getPersonAliases())
            {
                aliasIds.add(pa.getId() + "-PERSON-ALIAS");
            }
        }
        additionalProperties.put("person_alias_ss", aliasIds);
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Person.class.equals(acmObjectType);
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Person.class;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }
}
