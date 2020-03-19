package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;

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

import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/27/14.
 */
public class OrganizationToSolrTransformer implements AcmObjectToSolrDocTransformer<Organization>
{

    private OrganizationDao organizationDao;
    private UserDao userDao;
    private SearchAccessControlFields searchAccessControlFields;

    @Override
    public List<Organization> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getOrganizationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Organization org)
    {
        SolrAdvancedSearchDocument orgDoc = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(orgDoc, org);

        orgDoc.setId(org.getOrganizationId() + "-ORGANIZATION");
        orgDoc.setObject_type_s("ORGANIZATION");
        orgDoc.setObject_id_s(org.getOrganizationId() + "");

        orgDoc.setCreate_date_tdt(org.getCreated());
        orgDoc.setCreator_lcs(org.getCreator());
        orgDoc.setModified_date_tdt(org.getModified());
        orgDoc.setModifier_lcs(org.getModifier());

        orgDoc.setType_lcs(org.getOrganizationType());
        orgDoc.setValue_parseable(org.getOrganizationValue());

        orgDoc.setName(org.getOrganizationValue());
        orgDoc.setTitle_parseable(org.getOrganizationValue());
        orgDoc.setTitle_parseable_lcs(org.getOrganizationValue());
        orgDoc.setStatus_lcs(org.getStatus());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(org.getCreator());
        if (creator != null)
        {
            orgDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(org.getModifier());
        if (modifier != null)
        {
            orgDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        orgDoc.setAdditionalProperty("primary_contact_s", getPrimaryContact(org));
        orgDoc.setAdditionalProperty("default_phone_s", getDefaultPhone(org));
        orgDoc.setAdditionalProperty("default_location_s", getDefaultAddress(org));
        orgDoc.setAdditionalProperty("default_identification_s", getDefaultIdentification(org));

        String participantsListJson = ParticipantUtils.createParticipantsListJson(org.getParticipants());
        orgDoc.setAdditionalProperty("acm_participants_lcs", participantsListJson);

        return orgDoc;
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
    public SolrDocument toSolrQuickSearch(Organization in)
    {
        SolrDocument orgDoc = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(orgDoc, in);

        orgDoc.setId(in.getOrganizationId() + "-ORGANIZATION");
        orgDoc.setObject_type_s("ORGANIZATION");
        orgDoc.setObject_id_s(in.getOrganizationId() + "");

        orgDoc.setCreate_tdt(in.getCreated());
        orgDoc.setAuthor_s(in.getCreator());
        orgDoc.setLast_modified_tdt(in.getModified());
        orgDoc.setModifier_s(in.getModifier());

        orgDoc.setType_s(in.getOrganizationType());
        orgDoc.setData_s(in.getOrganizationValue());

        orgDoc.setName(in.getOrganizationValue());
        orgDoc.setTitle_parseable(in.getOrganizationValue());
        orgDoc.setTitle_parseable_lcs(in.getOrganizationValue());
        orgDoc.setStatus_s(in.getStatus());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            orgDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            orgDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return orgDoc;
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
