package com.armedia.acm.services.note.service;

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
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
        System.out.println("************************** January 18th ************************************************"+in.getParentTitle()+ "**********************************************");
        solr.setAdditionalProperty("type_s", in.getType());
        solr.setParent_ref_s(String.format("%d-%s", in.getParentId(), in.getParentType()));

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(Note in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(String.format("%d-%s", in.getId(), NoteConstants.OBJECT_TYPE));
        solrDoc.setObject_type_s(NoteConstants.OBJECT_TYPE);
        solrDoc.setName(String.format("%s_%d", NoteConstants.OBJECT_TYPE, in.getId()));
        solrDoc.setObject_id_s(in.getId() + "");
        solrDoc.setCreate_tdt(in.getCreated());
        solrDoc.setAuthor(in.getAuthor());
        solrDoc.setLast_modified_tdt(in.getModified());
        solrDoc.setType_s(in.getType());
        solrDoc.setAdditionalProperty("parent_object_type_s", in.getParentType());
        solrDoc.setAdditionalProperty("parent_object_id_i", in.getParentId());
        solrDoc.setAdditionalProperty("parent_number_lcs", in.getParentTitle());
        System.out.println("************************** January 18th ************************************************"+in.getParentTitle()+ "**********************************************");
        solrDoc.setParent_ref_s(String.format("%d-%s", in.getParentId(), in.getParentType()));
        solrDoc.setTitle_parseable(in.getNote());
        solrDoc.setAdditionalProperty("creator_s", in.getCreator());

        return solrDoc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(Note in)
    {
        return null;
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
