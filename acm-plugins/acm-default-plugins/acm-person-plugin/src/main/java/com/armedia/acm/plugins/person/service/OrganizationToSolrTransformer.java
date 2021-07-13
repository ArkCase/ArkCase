package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DATA_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.VALUE_PARSEABLE;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 10/27/14.
 */
public class OrganizationToSolrTransformer implements AcmObjectToSolrDocTransformer<Organization>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private OrganizationDao organizationDao;
    private UserDao userDao;
    private SearchAccessControlFields searchAccessControlFields;

    @Override
    public List<Organization> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getOrganizationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Organization in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for ORGANIZATION.");

        mapRequiredProperties(solrDoc, in.getOrganizationId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), in.getOrganizationValue());

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(Organization in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TYPE_LCS, in.getOrganizationType());
        additionalProperties.put(VALUE_PARSEABLE, in.getOrganizationValue());
        additionalProperties.put(TITLE_PARSEABLE, in.getOrganizationValue());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getOrganizationValue());
        additionalProperties.put(STATUS_LCS, in.getStatus());

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

        additionalProperties.put(DATA_LCS, in.getOrganizationValue());
        additionalProperties.put("primary_contact_s", getPrimaryContact(in));
        additionalProperties.put("default_phone_s", getDefaultPhone(in));
        additionalProperties.put("default_location_s", getDefaultAddress(in));
        additionalProperties.put("default_identification_s", getDefaultIdentification(in));
        additionalProperties.put("default_email_lcs", getDefaultEmail(in));

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        additionalProperties.put(ACM_PARTICIPANTS_LCS, participantsListJson);
    }

    private String getDefaultEmail(Organization organization)
    {
        if (organization.getDefaultEmail() != null)
        {
            return organization.getDefaultEmail().getValue();
        }
        else
        {
            return organization.getContactMethods().stream()
                    .filter(cm -> cm.getType().equalsIgnoreCase("email"))
                    .findFirst()
                    .map(ContactMethod::getValue)
                    .orElse(null);
        }
    }

    private String getPrimaryContact(Organization organization)
    {
        PersonOrganizationAssociation primaryContact = organization.getPrimaryContact();
        if (primaryContact == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Person person = primaryContact.getPerson();
        if (person != null)
        {
            if (person.getGivenName() != null
                    && !StringUtils.isEmpty(person.getGivenName().trim()))
            {
                sb.append(person.getGivenName());
            }
            if (person.getFamilyName() != null && !StringUtils.isEmpty(person.getFamilyName().trim()))
            {
                if (sb.length() > 0)
                {
                    sb.append(" ");
                }
                sb.append(person.getFamilyName());
            }
        }
        return sb.toString().trim();
    }

    private String getDefaultIdentification(Organization organization)
    {
        Identification identification = organization.getDefaultIdentification();
        if (identification == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (identification.getIdentificationNumber() != null && !StringUtils.isEmpty(identification.getIdentificationNumber().trim()))
        {
            sb.append(identification.getIdentificationNumber());
            if (identification.getIdentificationType() != null && !StringUtils.isEmpty(identification.getIdentificationType().trim()))
            {
                sb.append(" [" + identification.getIdentificationType() + "]");
            }
        }
        return sb.toString();
    }

    private String getDefaultPhone(Organization organization)
    {
        ContactMethod phoneContactMethod = organization.getDefaultPhone();
        if (phoneContactMethod == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (phoneContactMethod.getValue() != null && !StringUtils.isEmpty(phoneContactMethod.getValue().trim()))
        {
            sb.append(phoneContactMethod.getValue());
            if (phoneContactMethod.getSubType() != null && !StringUtils.isEmpty(phoneContactMethod.getSubType().trim()))
            {
                sb.append(" [" + phoneContactMethod.getSubType() + "]");
            }
        }
        return sb.toString();
    }

    private String getDefaultAddress(Organization organization)
    {
        PostalAddress address = organization.getDefaultAddress();
        if (address == null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (address.getCity() != null && !StringUtils.isEmpty(address.getCity().trim()))
        {
            sb.append(address.getCity());
            if (address.getState() != null && !StringUtils.isEmpty(address.getState().trim()))
            {
                if (sb.length() > 0)
                {
                    sb.append(", " + address.getState());
                }
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Organization.class.equals(acmObjectType);
    }

    public OrganizationDao getOrganizationDao()
    {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao)
    {
        this.organizationDao = organizationDao;
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
        return Organization.class;
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
