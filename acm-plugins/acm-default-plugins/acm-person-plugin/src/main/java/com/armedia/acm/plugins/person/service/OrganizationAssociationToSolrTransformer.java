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

import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.List;

public class OrganizationAssociationToSolrTransformer implements AcmObjectToSolrDocTransformer<OrganizationAssociation>
{
    private final Logger log = LogManager.getLogger(getClass());
    private OrganizationAssociationDao organizationAssociationDao;
    private UserDao userDao;

    @Override
    public List<OrganizationAssociation> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getOrganizationAssociationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(OrganizationAssociation organizationAssociation)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setId(organizationAssociation.getId() + "-ORGANIZATION-ASSOCIATION");
        solrDoc.setObject_id_s(organizationAssociation.getId() + "");
        solrDoc.setObject_type_s("ORGANIZATION-ASSOCIATION");
        solrDoc.setCreate_date_tdt(organizationAssociation.getCreated());
        solrDoc.setCreator_lcs(organizationAssociation.getCreator());
        solrDoc.setModified_date_tdt(organizationAssociation.getModified());
        solrDoc.setModifier_lcs(organizationAssociation.getModifier());

        solrDoc.setChild_id_s(organizationAssociation.getOrganization().getOrganizationId() + "");
        solrDoc.setChild_type_s("ORGANIZATION");
        solrDoc.setParent_id_s(organizationAssociation.getParentId() + "");
        solrDoc.setParent_type_s(organizationAssociation.getParentType());
        solrDoc.setParent_number_lcs(organizationAssociation.getParentTitle());

        solrDoc.setType_lcs(organizationAssociation.getAssociationType());

        solrDoc.setName(organizationAssociation.getOrganization().getOrganizationValue() + " ("
                + organizationAssociation.getAssociationType() + ")");

        solrDoc.setTitle_parseable(organizationAssociation.getOrganization().getOrganizationValue() + " ("
                + organizationAssociation.getAssociationType() + ")");

        solrDoc.setParent_ref_s(organizationAssociation.getParentId() + "-" + organizationAssociation.getParentType());

        solrDoc.setDescription_parseable(organizationAssociation.getDescription());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(organizationAssociation.getCreator());
        if (creator != null)
        {
            solrDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(organizationAssociation.getModifier());
        if (modifier != null)
        {
            solrDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return solrDoc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(OrganizationAssociation in)
    {
        // we don't want organization associations in quick search, so just return null
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return OrganizationAssociation.class.equals(acmObjectType);
    }

    public OrganizationAssociationDao getOrganizationAssociationDao()
    {
        return organizationAssociationDao;
    }

    public void setOrganizationAssociationDao(OrganizationAssociationDao organizationAssociationDao)
    {
        this.organizationAssociationDao = organizationAssociationDao;
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
        return OrganizationAssociation.class;
    }
}
