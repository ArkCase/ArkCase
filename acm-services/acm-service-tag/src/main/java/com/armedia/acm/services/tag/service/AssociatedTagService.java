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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.model.TagConfig;
import com.armedia.acm.services.tag.model.TagConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 31.03.2015.
 */
public class AssociatedTagService
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private TagDao tagDao;
    private AssociatedTagDao associatedTagDao;
    private TagConfig tagConfig;
    private ExecuteSolrQuery executeSolrQuery;

    public List<AcmAssociatedTag> getAcmAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long objectId, String objectType)
    {
        return getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId, objectId, objectType);
    }

    public AcmAssociatedTag getAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long objectId, String objectType)
    {
        return getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId, objectId, objectType).get(TagConstants.ZERO);
    }

    public int removeAssociatedTag(Long tagId, Long objectId, String objectType) throws SQLException
    {
        AcmAssociatedTag acmAssociatedTag = getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId, objectId, objectType)
                .get(TagConstants.ZERO);
        return getAssociatedTagDao().deleteAssociateTag(tagId, objectId, objectType);
    }

    public List<AcmAssociatedTag> getAcmAssociatedTagsByObjectIdAndType(Long objectId, String objectType, Authentication auth)
            throws AcmObjectNotFoundException
    {
        JSONObject associatedTags = findTagsInSolrByParentTypeAndParentId(objectType, objectId, auth);
        return prepareTagList(objectType, objectId, associatedTags);
    }

    private JSONObject findTagsInSolrByParentTypeAndParentId(String objectType, Long objectId, Authentication auth)
            throws AcmObjectNotFoundException
    {

        String predefinedQuery = tagConfig.getTagAssociatedByObjectIdAndTypeQuery();
        predefinedQuery = predefinedQuery.replace(TagConstants.SOLR_PLACEHOLDER_PARENT_TYPE, objectType);
        predefinedQuery = predefinedQuery.replace(TagConstants.SOLR_PLACEHOLDER_PARENT_ID, Long.toString(objectId));
        String solrResponseJsonString;
        try
        {
            solrResponseJsonString = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH,
                    predefinedQuery, TagConstants.FIRST_ROW, TagConstants.MAX_ROWS, TagConstants.SORT);
        }
        catch (SolrException e)
        {
            log.error("Solr exception occurred while performing advanced search to fetch tags for object [{}]:[{}]",
                    objectType, objectId, e);
            throw new AcmObjectNotFoundException(TagConstants.OBJECT_TYPE, null,
                    "Solr exception occurred while performing advanced search to fetch tags for object['" + objectType + "]:[" + objectId
                            + "]",
                    e);
        }
        return new JSONObject(solrResponseJsonString);
    }

    private List<AcmAssociatedTag> prepareTagList(String objectType, Long objectId, JSONObject solrResponse)
            throws AcmObjectNotFoundException
    {

        List<AcmAssociatedTag> associatedTags = new ArrayList<>();
        JSONObject responseBody = solrResponse.getJSONObject(TagConstants.SOLR_RESPONSE_BODY);
        JSONArray docsList = responseBody.getJSONArray(TagConstants.SOLR_RESPONSE_DOCS);
        if (docsList.length() == 0)
        {
            log.error("No tags associated to the object[{}]:[{}]", objectType, objectId);
            throw new AcmObjectNotFoundException(objectType, objectId, "no such object to subscribe to", null);
        }
        for (int i = 0; i < docsList.length(); i++)
        {
            String associatedTagId = docsList.getJSONObject(i).getString(TagConstants.SOLR_ID)
                    .split(TagConstants.SOLR_ID_SPLITER)[TagConstants.ZERO];
            AcmAssociatedTag associatedTag = getAssociatedTagDao().find(Long.parseLong(associatedTagId));
            if (associatedTag != null)
            {
                associatedTags.add(associatedTag);
            }
        }
        return associatedTags;
    }

    public int removeAssociatedTag(AcmAssociatedTag acmAssociatedTag) throws SQLException
    {
        return getAssociatedTagDao().deleteAssociateTag(acmAssociatedTag.getTag().getId(), acmAssociatedTag.getParentId(),
                acmAssociatedTag.getParentType());
    }

    public AcmAssociatedTag saveAssociateTag(String objectType, Long objectId, String objectTitle, AcmTag tag)
    {

        // TODO check if exists objectId with objectType if not throw exception

        AcmAssociatedTag acmAssociatedTag = new AcmAssociatedTag();
        acmAssociatedTag.setParentType(objectType);
        acmAssociatedTag.setParentId(objectId);
        acmAssociatedTag.setParentTitle(objectTitle);
        acmAssociatedTag.setTag(tag);
        return getAssociatedTagDao().save(acmAssociatedTag);
    }

    public AcmAssociatedTag saveAssociateTag(String objectType, Long objectId, String objectTitle, Long tagId)
            throws AcmObjectNotFoundException
    {

        AcmAssociatedTag acmAssociatedTag = new AcmAssociatedTag();
        acmAssociatedTag.setParentType(objectType);
        acmAssociatedTag.setParentId(objectId);
        acmAssociatedTag.setParentTitle(objectTitle);
        AcmTag tag;
        try
        {
            tag = getTagDao().find(tagId);
        }
        catch (Exception e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Not existing", e);
            }
            throw new AcmObjectNotFoundException(AcmTag.OBJECT_TYPE, tagId, "", e);
        }
        acmAssociatedTag.setTag(tag);
        AcmAssociatedTag newAssociatedTag = getAssociatedTagDao().save(acmAssociatedTag);
        return newAssociatedTag;
    }

    public TagConfig getTagConfig()
    {
        return tagConfig;
    }

    public void setTagConfig(TagConfig tagConfig)
    {
        this.tagConfig = tagConfig;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public TagDao getTagDao()
    {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao)
    {
        this.tagDao = tagDao;
    }

    public AssociatedTagDao getAssociatedTagDao()
    {
        return associatedTagDao;
    }

    public void setAssociatedTagDao(AssociatedTagDao associatedTagDao)
    {
        this.associatedTagDao = associatedTagDao;
    }
}
