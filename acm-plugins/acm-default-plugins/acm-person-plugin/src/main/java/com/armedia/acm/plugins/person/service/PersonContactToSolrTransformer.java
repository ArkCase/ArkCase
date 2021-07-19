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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CONTACT_METHOD_SS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.FIRST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.LAST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.POSTAL_ADDRESS_ID_SS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonContactDao;
import com.armedia.acm.plugins.person.model.PersonContact;
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
 * Created by riste.tutureski on 10/21/14.
 */
public class PersonContactToSolrTransformer implements AcmObjectToSolrDocTransformer<PersonContact>
{
    private final Logger log = LogManager.getLogger(getClass());
    private PersonContactDao personContactDao;
    private UserDao userDao;

    @Override
    public List<PersonContact> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonContactDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonContact in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        log.debug("Creating Solr advanced search document for PERSON_CONTACT.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                "PERSON_CONTACT", "");

        if (in.getCompanyName() != null && !in.getCompanyName().isEmpty())
        {
            solrDoc.setName(in.getCompanyName());
        }
        else if (in.getPersonName() != null && !in.getPersonName().isEmpty())
        {
            solrDoc.setName(in.getPersonName());
        }

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(PersonContact in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(FIRST_NAME_LCS, in.getFirstName());
        additionalProperties.put(LAST_NAME_LCS, in.getLastName());
        additionalProperties.put(TITLE_PARSEABLE, in.getFirstName() + " " + in.getLastName());

        addContactMethods(in, additionalProperties);

        addAddresses(in, additionalProperties);

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
    }

    private void addAddresses(PersonContact personContact, Map<String, Object> additionalProperties)
    {
        List<String> addressIds = new ArrayList<>();
        if (personContact.getAddresses() != null)
        {
            for (PostalAddress address : personContact.getAddresses())
            {
                addressIds.add(address.getId() + "-LOCATION");
            }

        }
        additionalProperties.put(POSTAL_ADDRESS_ID_SS, addressIds);
    }

    private void addContactMethods(PersonContact personContact, Map<String, Object> additionalProperties)
    {
        List<String> contactMethodIds = new ArrayList<>();
        if (personContact.getContactMethods() != null)
        {
            for (ContactMethod cm : personContact.getContactMethods())
            {
                contactMethodIds.add(cm.getId() + "-CONTACT-METHOD");
            }
        }
        additionalProperties.put(CONTACT_METHOD_SS, contactMethodIds);
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return PersonContact.class.equals(acmObjectType);
    }

    public PersonContactDao getPersonContactDao()
    {
        return personContactDao;
    }

    public void setPersonContactDao(PersonContactDao personContactDao)
    {
        this.personContactDao = personContactDao;
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
        return PersonContact.class;
    }
}
