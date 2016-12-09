package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 08.11.2014.
 */
public class CaseFileToSolrTransformer implements AcmObjectToSolrDocTransformer<CaseFile>
{

    private UserDao userDao;
    private CaseFileDao caseFileDao;
    private SearchAccessControlFields searchAccessControlFields;

    @Override
    public List<CaseFile> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getCaseFileDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(CaseFile in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setId(in.getId() + "-CASE_FILE");
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s("CASE_FILE");
        solr.setTitle_parseable(in.getTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setName(in.getCaseNumber());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setDueDate_tdt(in.getDueDate());

        solr.setIncident_date_tdt(in.getIncidentDate());
        solr.setPriority_lcs(in.getPriority());
        solr.setIncident_type_lcs(in.getCaseType());
        solr.setStatus_lcs(in.getStatus());

        String assigneeUserId = findAssigneeUserId(in);
        solr.setAssignee_id_lcs(assigneeUserId);

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

        solr.setAdditionalProperty("security_field_lcs", in.getSecurityField());

        solr.setTitle_parseable_lcs(in.getTitle());

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        solr.setAdditionalProperty("acm_participants_lcs", participantsListJson);

        solr.setAdditionalProperty("assignee_group_id_lcs", in.getAssigneeGroup());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(CaseFile in)
    {
        SolrDocument solr = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setName(in.getCaseNumber());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s("CASE_FILE");
        solr.setId(in.getId() + "-CASE_FILE");

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getStatus());

        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setTitle_parseable(in.getTitle());

        String assigneeUserId = findAssigneeUserId(in);
        solr.setAssignee_s(assigneeUserId);

        // needed a _lcs property for sorting
        solr.setTitle_parseable_lcs(in.getTitle());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(CaseFile in)
    {
        // No implementation needed
        return null;
    }

    private String findAssigneeUserId(CaseFile in)
    {
        if (in.getParticipants() != null)
        {
            for (AcmParticipant participant : in.getParticipants())
            {
                if ("assignee".equals(participant.getParticipantType()))
                {
                    return participant.getParticipantLdapId();
                }
            }
        }

        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return CaseFile.class.equals(acmObjectType);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return CaseFile.class;
    }
}
