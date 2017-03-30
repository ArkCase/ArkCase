package com.armedia.acm.plugins.documentrepository.service;

import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;


public class DocumentRepositoryToSolrTransformer implements AcmObjectToSolrDocTransformer<DocumentRepository>
{
    private UserDao userDao;
    private SearchAccessControlFields searchAccessControlFields;
    private DocumentRepositoryDao documentRepositoryDao;

    @Override
    public List<DocumentRepository> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return documentRepositoryDao.findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(DocumentRepository in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setId(String.format("%d-%s", in.getId(), in.getObjectType()));
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_type_s(DocumentRepositoryConstants.OBJECT_TYPE);
        solr.setTitle_parseable(in.getName());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setName(in.getName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setStatus_lcs(in.getStatus());

        String assigneeUserId = ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants());
        solr.setAssignee_id_lcs(assigneeUserId);

        solr.setTitle_parseable_lcs(in.getName());

        AcmUser assignee = getUserDao().quietFindByUserId(assigneeUserId);

        if (assignee != null)
        {
            solr.setAssignee_first_name_lcs(assignee.getFirstName());
            solr.setAssignee_last_name_lcs(assignee.getLastName());
            solr.setAssignee_full_name_lcs(assignee.getFirstName() + " " + assignee.getLastName());
        }

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

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        solr.setAdditionalProperty("acm_participants_lcs", participantsListJson);

        // This property is used for show in the owning group for the object
        solr.setAdditionalProperty("owning_group_id_lcs", ParticipantUtils.getOwningGroupIdFromParticipants(in.getParticipants()));

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(DocumentRepository in)
    {
        SolrDocument solr = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setId(String.format("%d-%s", in.getId(), in.getObjectType()));
        solr.setName(in.getName());
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_type_s(DocumentRepositoryConstants.OBJECT_TYPE);

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setTitle_parseable(in.getName());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setStatus_s(in.getStatus());

        String assigneeUserId = ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants());
        solr.setAssignee_s(assigneeUserId);

        // needed a _lcs property for sorting
        solr.setTitle_parseable_lcs(in.getName());
        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(DocumentRepository in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return DocumentRepository.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return DocumentRepository.class;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }

    public DocumentRepositoryDao getDocumentRepositoryDao()
    {
        return documentRepositoryDao;
    }

    public void setDocumentRepositoryDao(DocumentRepositoryDao documentRepositoryDao)
    {
        this.documentRepositoryDao = documentRepositoryDao;
    }
}
