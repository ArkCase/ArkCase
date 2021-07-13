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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CHILD_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CHILD_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NUMBER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;

import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(OrganizationAssociation in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        log.debug("Creating Solr advanced search document for ORGANIZATION-ASSOCIATION.");

        String name = in.getOrganization().getOrganizationValue() + " (" + in.getAssociationType() + ")";
        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(),
                in.getModified(), PersonOrganizationConstants.ORGANIZATION_ASSOCIATION_OBJECT_TYPE, name);


        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(OrganizationAssociation in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(CHILD_ID_S, in.getOrganization().getOrganizationId() + "");
        additionalProperties.put(CHILD_TYPE_S, PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE);
        additionalProperties.put(PARENT_ID_S, in.getParentId() + "");
        additionalProperties.put(PARENT_TYPE_S, in.getParentType());
        additionalProperties.put(PARENT_NUMBER_LCS, in.getParentTitle());
        additionalProperties.put(TYPE_LCS, in.getAssociationType());
        additionalProperties.put(TITLE_PARSEABLE, in.getOrganization().getOrganizationValue() + " (" + in.getAssociationType() + ")");
        additionalProperties.put(PARENT_REF_S, in.getParentId() + "-" + in.getParentType());
        additionalProperties.put(DESCRIPTION_PARSEABLE, in.getDescription());

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
