package com.armedia.acm.plugins.addressable.service;

/*-
 * #%L
 * ACM Default Plugin: Addressable
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.VALUE_PARSEABLE;

import com.armedia.acm.plugins.addressable.dao.ContactMethodDao;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 10/27/14.
 */
public class ContactMethodToSolrTransformer implements AcmObjectToSolrDocTransformer<ContactMethod>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private ContactMethodDao contactMethodDao;
    private UserDao userDao;

    @Override
    public List<ContactMethod> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getContactMethodDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(ContactMethod in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for CONTACT-METHOD.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                "CONTACT-METHOD", in.getValue());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(ContactMethod in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TYPE_LCS, in.getType());
        additionalProperties.put(VALUE_PARSEABLE, in.getValue());
        additionalProperties.put(TITLE_PARSEABLE, in.getValue());

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

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return ContactMethod.class.equals(acmObjectType);
    }

    public ContactMethodDao getContactMethodDao()
    {
        return contactMethodDao;
    }

    public void setContactMethodDao(ContactMethodDao contactMethodDao)
    {
        this.contactMethodDao = contactMethodDao;
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
        return ContactMethod.class;
    }
}
