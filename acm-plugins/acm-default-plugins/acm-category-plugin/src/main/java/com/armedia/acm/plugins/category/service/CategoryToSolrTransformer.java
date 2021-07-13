package com.armedia.acm.plugins.category.service;

/*-
 * #%L
 * ACM Default Plugin: Categories
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
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_NO_HTML_TAGS_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;

import com.armedia.acm.plugins.category.dao.CategoryDao;
import com.armedia.acm.plugins.category.model.Category;
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
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 13, 2017
 *
 */
public class CategoryToSolrTransformer implements AcmObjectToSolrDocTransformer<Category>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private UserDao userDao;

    private CategoryDao categoryDao;

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#getObjectsModifiedSince(java.util.Date,
     * int, int)
     */
    @Override
    public List<Category> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return categoryDao.findModifiedSince(lastModified, start, pageSize);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#toSolrAdvancedSearch(java.lang.Object)
     */
    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Category in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for CATEGORY.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), in.getName());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

       return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(Category in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getName());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getName());
        additionalProperties.put(DESCRIPTION_NO_HTML_TAGS_PARSEABLE, in.getDescription());

        if (in.getStatus() != null)
        {
            additionalProperties.put(STATUS_LCS, in.getStatus().name());
        }

        if (in.getParent() != null)
        {
            additionalProperties.put(PARENT_ID_S, in.getParent().getId().toString());
            additionalProperties.put(PARENT_TYPE_S, "CATEGORY");
        }

        /** Additional properties for full names instead of ID's */
        AcmUser creator = userDao.quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, String.format("%s %s", creator.getFirstName(), creator.getLastName()));
        }

        AcmUser modifier = userDao.quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, String.format("%s %s", modifier.getFirstName(), modifier.getLastName()));
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#isAcmObjectTypeSupported(java.lang.Class)
     */
    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Category.class.equals(acmObjectType);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#getAcmObjectTypeSupported()
     */
    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Category.class;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @param categoryDao
     *            the categoryDao to set
     */
    public void setCategoryDao(CategoryDao categoryDao)
    {
        this.categoryDao = categoryDao;
    }

}
