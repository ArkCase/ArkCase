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

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 11.11.2014.
 */
public class UserToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmUser>
{

    private UserDao userDao;

    @Override
    public List<AcmUser> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getUserDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmUser in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();
        solr.setId(in.getUserId() + "-USER");
        solr.setObject_id_s(in.getUserId() + "");
        solr.setObject_type_s("USER");
        solr.setName(in.getFullName());
        solr.setFirst_name_lcs(in.getFirstName());
        solr.setLast_name_lcs(in.getLastName());
        solr.setEmail_lcs(in.getMail());

        solr.setTitle_parseable(in.getFullName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setModified_date_tdt(in.getModified());

        solr.setStatus_lcs(in.getUserState().name());

        // Add groups
        solr.setGroups_id_ss(in.getGroupNames().count() == 0 ? null : in.getGroupNames().collect(Collectors.toList()));

        solr.setAdditionalProperty("directory_name_s", in.getUserDirectoryName());
        solr.setAdditionalProperty("country_s", in.getCountry());
        solr.setAdditionalProperty("country_abbreviation_s", in.getCountryAbbreviation());
        solr.setAdditionalProperty("department_s", in.getDepartment());
        solr.setAdditionalProperty("company_s", in.getCompany());
        solr.setAdditionalProperty("title_s", in.getTitle());
        solr.setAdditionalProperty("name_partial", in.getFullName());
        solr.setAdditionalProperty("name_lcs", in.getFullName());

        // TODO find a way to add Organization
        // TODO find a way to add Application Title
        // TODO find a way to add Location

        return solr;
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
