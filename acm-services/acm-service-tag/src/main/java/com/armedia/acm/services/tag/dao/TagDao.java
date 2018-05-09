package com.armedia.acm.services.tag.dao;

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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.tag.model.AcmTag;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
public class TagDao extends AcmAbstractDao<AcmTag>
{

    @Override
    protected Class<AcmTag> getPersistenceClass()
    {
        return AcmTag.class;
    }

    public AcmTag getTagByTextOrDescOrName(String text, String desc, String name)
    {

        Query query = getEm().createQuery(
                "SELECT tag FROM AcmTag tag " +
                        "WHERE tag.tagText =:text " +
                        "OR tag.tagName =:name " +
                        "OR tag.tagDescription =:desc ");
        query.setParameter("text", text);
        query.setParameter("name", name);
        query.setParameter("desc", desc);

        List<AcmTag> results = query.getResultList();
        AcmTag existingTag = null;
        if (!results.isEmpty())
        {
            existingTag = results.get(0);
        }
        return existingTag;
    }

    public AcmTag getTagByText(String tagText)
    {

        Query query = getEm().createQuery(
                "SELECT tag FROM AcmTag tag " +
                        "WHERE tag.tagText =:tagText ");
        query.setParameter("tagText", tagText);

        List<AcmTag> results = query.getResultList();
        AcmTag tag = null;
        if (!results.isEmpty())
        {
            tag = results.get(0);
        }
        return tag;
    }

    public AcmTag getTagByName(String tagName)
    {

        Query query = getEm().createQuery(
                "SELECT tag FROM AcmTag tag " +
                        "WHERE tag.tagName =:tagName ");
        query.setParameter("tagName", tagName);
        List<AcmTag> results = query.getResultList();
        AcmTag tag = null;
        if (!results.isEmpty())
        {
            tag = results.get(0);
        }
        return tag;
    }

    @Transactional
    public void deleteTag(AcmTag tag) throws SQLException
    {
        getEm().remove(tag);
    }

    @Transactional
    public AcmTag updateTag(AcmTag tag) throws SQLException
    {
        tag = getEm().merge(tag);
        return tag;
    }
}
