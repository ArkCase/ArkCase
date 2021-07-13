package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.EMAIL_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.FIRST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.LAST_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 11.11.2014.
 */
public class UserToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmUser>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private UserDao userDao;

    @Override
    public List<AcmUser> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getUserDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmUser in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for USER.");

        solrDoc.setId(in.getUserId() + "-USER");
        solrDoc.setObject_id_s(in.getUserId() + "");
        solrDoc.setObject_type_s("USER");
        solrDoc.setName(in.getFullName());
        solrDoc.setName_lcs(in.getFullName());
        solrDoc.setCreate_date_tdt(in.getCreated());
        solrDoc.setModified_date_tdt(in.getModified());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmUser in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(FIRST_NAME_LCS, in.getFirstName());
        additionalProperties.put(LAST_NAME_LCS, in.getLastName());
        additionalProperties.put(EMAIL_LCS, in.getMail());
        additionalProperties.put(TITLE_PARSEABLE, in.getFullName());
        additionalProperties.put(STATUS_LCS, in.getUserState().name());

        // Add groups
        additionalProperties.put("groups_id_ss", in.getGroupNames().count() == 0 ? null : in.getGroupNames().collect(Collectors.toList()));

        additionalProperties.put("directory_name_s", in.getUserDirectoryName());
        additionalProperties.put("country_s", in.getCountry());
        additionalProperties.put("country_abbreviation_s", in.getCountryAbbreviation());
        additionalProperties.put("department_s", in.getDepartment());
        additionalProperties.put("company_s", in.getCompany());
        additionalProperties.put("title_s", in.getTitle());
        additionalProperties.put("name_partial", in.getFullName());

        // TODO find a way to add Organization
        // TODO find a way to add Application Title
        // TODO find a way to add Location
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmUser.class.equals(acmObjectType);
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
        return AcmUser.class;
    }

}
