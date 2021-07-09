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

import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by ana.serafimoska
 */
public class OrganizationEmailToSolrTransformer implements AcmObjectToSolrDocTransformer<Organization>
{
    private OrganizationDao organizationDao;
    private UserDao userDao;
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public List<Organization> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getOrganizationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Organization in)
    {
        if (in.getDefaultEmail() != null)
        {
            SolrAdvancedSearchDocument solrDocument = new SolrAdvancedSearchDocument();
            if (in.getDefaultEmail() != null)
            {
                solrDocument.setObject_type_s("EMAIL");
                solrDocument.setId(in.getId() + "-EMAIL");
                solrDocument.setObject_id_s(in.getOrganizationId() + "");
                solrDocument.setType_lcs(in.getObjectType());
                solrDocument.setEmail_lcs(in.getDefaultEmail().getValue());
                solrDocument.setName(in.getOrganizationValue());

            }
            return solrDocument;
        }
        else
        {
            log.info("Organization has no default email. No EMAIL solr document will be added");
            return null;
        }
    }

    // No implementation needed due to https://arkcase.atlassian.net/browse/ACFP-704
    @Override
    public SolrDocument toSolrQuickSearch(Organization in)
    {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Organization.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Organization.class;
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

}
