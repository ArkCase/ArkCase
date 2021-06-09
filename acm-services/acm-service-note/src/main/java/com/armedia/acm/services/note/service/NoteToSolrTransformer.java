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

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

public class NoteToSolrTransformer implements AcmObjectToSolrDocTransformer<Note>
{

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
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(String.format("%d-%s", in.getId(), NoteConstants.OBJECT_TYPE));

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(NoteConstants.OBJECT_TYPE);

        solr.setDescription_parseable(in.getNote());
        solr.setTitle_parseable(in.getNote());
        solr.setName(String.format("%s_%d", NoteConstants.OBJECT_TYPE, in.getId()));

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());

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

        AcmUser author = getUserDao().quietFindByUserId(in.getAuthor());
        if (author != null)
        {
            solr.setAdditionalProperty("author_full_name_lcs", author.getFirstName() + " " + author.getLastName());
        }

        solr.setAdditionalProperty("parent_object_type_s", in.getParentType());
        solr.setAdditionalProperty("parent_object_id_i", in.getParentId());
        solr.setAdditionalProperty("parent_number_lcs", in.getParentTitle());
        solr.setAdditionalProperty("type_s", in.getType());
        solr.setParent_ref_s(String.format("%d-%s", in.getParentId(), in.getParentType()));

        return solr;
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
