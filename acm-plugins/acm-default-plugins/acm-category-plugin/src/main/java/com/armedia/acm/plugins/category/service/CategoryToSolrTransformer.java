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

import com.armedia.acm.plugins.category.dao.CategoryDao;
import com.armedia.acm.plugins.category.model.Category;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 13, 2017
 *
 */
public class CategoryToSolrTransformer implements AcmObjectToSolrDocTransformer<Category>
{

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
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-CATEGORY");
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s("CATEGORY");
        solr.setTitle_parseable(in.getName());
        solr.setTitle_parseable_lcs(in.getName());
        solr.setDescription_no_html_tags_parseable(in.getDescription());
        solr.setName(in.getName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        if (in.getStatus() != null)
        {
            solr.setStatus_lcs(in.getStatus().name());
        }

        if (in.getParent() != null)
        {
            solr.setParent_id_s(in.getParent().getId().toString());
            solr.setParent_type_s("CATEGORY");
        }

        /** Additional properties for full names instead of ID's */
        AcmUser creator = userDao.quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", String.format("%s %s", creator.getFirstName(), creator.getLastName()));
        }

        AcmUser modifier = userDao.quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", String.format("%s %s", modifier.getFirstName(), modifier.getLastName()));
        }

        return solr;
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
