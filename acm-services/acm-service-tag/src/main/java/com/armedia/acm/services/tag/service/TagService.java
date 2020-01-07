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

import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.model.TagConfig;
import com.armedia.acm.services.tag.model.TagConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 30.03.2015.
 */
public class TagService
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private TagDao tagDao;
    private TagConfig tagConfig;

    public List<AcmTag> getAllTags()
    {
        List<AcmTag> tags = getTagDao().findAll();
        // If there are no tags in the DB, add predefined tags from property file
        // if property file did not contains predefined tags return []!
        if (tags.isEmpty())
        {
            List<AcmTag> newTags = prepareTagsFromPropertiesFile();
            for (AcmTag tag : newTags)
            {
                tags.add(getTagDao().save(tag));
            }
        }
        return tags;
    }

    public AcmTag findTag(Long tagId)
    {
        return getTagDao().find(tagId);
    }

    public AcmTag saveTag(String name, String desc, String value)
    {

        AcmTag newTag = new AcmTag();
        newTag.setTagText(value);
        newTag.setTagName(name);
        newTag.setTagDescription(desc);
        newTag.setTagToken(value + "-" + System.currentTimeMillis());

        return getTagDao().save(newTag);
    }

    public AcmTag getTagByTextOrDescOrName(String text, String desc, String name)
    {
        return getTagDao().getTagByTextOrDescOrName(text, desc, name);
    }

    public void removeTag(Long tagId) throws SQLException
    {
        AcmTag tag = getTagDao().find(tagId);
        getTagDao().deleteTag(tag);
    }

    public void removeTag(AcmTag tag) throws SQLException
    {
        getTagDao().deleteTag(tag);
    }

    public AcmTag updateTag(Long tagId, String name, String text, String desc) throws SQLException
    {
        AcmTag tagForUpdate = getTagDao().find(tagId);
        tagForUpdate.setTagName(name);
        tagForUpdate.setTagText(text);
        tagForUpdate.setTagDescription(desc);
        return getTagDao().save(tagForUpdate);
    }

    private List<AcmTag> prepareTagsFromPropertiesFile()
    {
        List<AcmTag> tags = new ArrayList<>();
        String jsonTagsString = tagConfig.getTags();

        JSONArray allTagsJsonArray = new JSONArray(jsonTagsString);
        for (int i = 0; i < allTagsJsonArray.length(); i++)
        {
            JSONObject tagObject = allTagsJsonArray.getJSONObject(i);
            tags.add(prepareTagFromJsonObject(tagObject));
        }
        return tags;
    }

    private AcmTag prepareTagFromJsonObject(JSONObject jsonObject)
    {
        AcmTag tag = new AcmTag();
        tag.setTagName(jsonObject.getString(TagConstants.TAG_NAME));
        tag.setTagDescription(jsonObject.getString(TagConstants.TAG_DESC));
        tag.setTagText(jsonObject.getString(TagConstants.TAG_VALUE));
        tag.setTagToken(tag.getTagText() + "-" + System.currentTimeMillis());
        return tag;
    }

    public TagDao getTagDao()
    {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao)
    {
        this.tagDao = tagDao;
    }

    public TagConfig getTagConfig()
    {
        return tagConfig;
    }

    public void setTagConfig(TagConfig tagConfig)
    {
        this.tagConfig = tagConfig;
    }
}
