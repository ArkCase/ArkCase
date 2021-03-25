package com.armedia.acm.services.tag.service;

/*-
 * #%L
 * ACM Service: Tag
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
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 13.04.2015.
 */
public class AssociatedTagToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmAssociatedTag>
{

    private AssociatedTagDao associatedTagDao;
    private UserDao userDao;

    @Override
    public List<AcmAssociatedTag> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getAssociatedTagDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmAssociatedTag in)
    {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setParent_type_s(in.getParentType());
        solr.setParent_id_s(Long.toString(in.getParentId()));
        solr.setParent_ref_s(Long.toString(in.getParentId()) + "-" + in.getParentType());
        solr.setAdditionalProperty("parent_number_lcs", in.getParentTitle());

        solr.setTag_token_lcs(in.getTag().getTagToken().substring(0, in.getTag().getTagToken().indexOf("-")));

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        solr.setAdditionalProperty("title_parseable",
                in.getTag().getTagText() + " on object of type " + in.getParentType() + " and ID: " + in.getParentId());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmAssociatedTag in)
    {

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_tdt(in.getCreated());
        solr.setAuthor(in.getCreator());
        solr.setLast_modified_tdt(in.getModified());
        solr.setModifier_s(in.getModifier());

        solr.setParent_object_id_s(Long.toString(in.getParentId()));

        solr.setParent_object_type_s(in.getParentType());

        solr.setParent_ref_s(Long.toString(in.getParentId()) + "-" + in.getParentType());

        solr.setAdditionalProperty("parent_number_lcs", in.getParentTitle());

        solr.setTag_token_lcs(in.getTag().getTagToken());

        solr.setAdditionalProperty("title_parseable",
                in.getTag().getTagText() + " on object of type " + in.getParentType() + " and ID: " + in.getParentId());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmAssociatedTag.class.equals(acmObjectType);
    }

    public AssociatedTagDao getAssociatedTagDao()
    {
        return associatedTagDao;
    }

    public void setAssociatedTagDao(AssociatedTagDao associatedTagDao)
    {
        this.associatedTagDao = associatedTagDao;
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
        return AcmAssociatedTag.class;
    }
}
