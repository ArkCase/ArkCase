package com.armedia.acm.services.note.service;

/*-
 * #%L
 * ACM Service: Note
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
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NUMBER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_OBJECT_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class NoteToSolrTransformer implements AcmObjectToSolrDocTransformer<Note>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private UserDao userDao;
    private NoteDao noteDao;

    @Override
    public List<Note> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getNoteDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Note in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for NOTE.");

        String name = String.format("%s_%d", NoteConstants.OBJECT_TYPE, in.getId());

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                NoteConstants.OBJECT_TYPE, name);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(Note in, Map<String, Object> additionalProperties)
    {
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

        AcmUser author = getUserDao().quietFindByUserId(in.getAuthor());
        if (author != null)
        {
            additionalProperties.put("author_full_name_lcs", author.getFirstName() + " " + author.getLastName());
        }

        additionalProperties.put(DESCRIPTION_PARSEABLE, in.getNote());
        additionalProperties.put(TITLE_PARSEABLE, in.getNote());
        additionalProperties.put(PARENT_TYPE_S, in.getParentType());
        additionalProperties.put(PARENT_OBJECT_ID_I, in.getParentId());
        additionalProperties.put(PARENT_NUMBER_LCS, in.getParentTitle());
        additionalProperties.put(TYPE_LCS, in.getType());
        additionalProperties.put(PARENT_REF_S, String.format("%d-%s", in.getParentId(), in.getParentType()));
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Note.class.equals(acmObjectType);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public NoteDao getNoteDao()
    {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Note.class;
    }
}
