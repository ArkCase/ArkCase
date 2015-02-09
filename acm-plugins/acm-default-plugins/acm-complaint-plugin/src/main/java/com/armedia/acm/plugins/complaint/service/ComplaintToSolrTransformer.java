package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.SearchAccessControlFields;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/28/14.
 */
public class ComplaintToSolrTransformer implements AcmObjectToSolrDocTransformer<Complaint>
{
    private UserDao userDao;
    private ComplaintDao complaintDao;
    private SearchAccessControlFields searchAccessControlFields;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<Complaint> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getComplaintDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Complaint in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setId(in.getComplaintId() + "-COMPLAINT");
        solr.setObject_id_s(in.getComplaintId() + "");
        solr.setObject_type_s("COMPLAINT");
        solr.setTitle_parseable(in.getComplaintTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setName(in.getComplaintNumber());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setDueDate_tdt(in.getDueDate());

        solr.setIncident_date_tdt(in.getIncidentDate());
        solr.setPriority_lcs(in.getPriority());
        solr.setIncident_type_lcs(in.getComplaintType());
        solr.setStatus_lcs(in.getStatus());

        String assigneeUserId = findAssigneeUserId(in);
        solr.setAssignee_id_lcs(assigneeUserId);

        AcmUser assignee = getUserDao().quietFindByUserId(assigneeUserId);

        if ( assignee != null )
        {
            solr.setAssignee_first_name_lcs(assignee.getFirstName());
            solr.setAssignee_last_name_lcs(assignee.getLastName());
            solr.setAssignee_full_name_lcs(assignee.getFirstName()+" "+assignee.getLastName());
        }

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(Complaint in)
    {
        SolrDocument solr = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        solr.setName(in.getComplaintNumber());
        solr.setObject_id_s(in.getComplaintId() + "");
        solr.setObject_type_s("COMPLAINT");
        solr.setId(in.getComplaintId() + "-COMPLAINT");

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setTitle_parseable(in.getComplaintTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setStatus_s(in.getStatus());

        String assigneeUserId = findAssigneeUserId(in);
        solr.setAssignee_s(assigneeUserId);

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(Complaint in) {
        //No implementation needed
        return null;
    }

    private String findAssigneeUserId(Complaint in)
    {
        if ( in.getParticipants() != null )
        {
            for ( AcmParticipant participant : in.getParticipants() )
            {
                if ( "assignee".equals(participant.getParticipantType()) )
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
        boolean objectNotNull = acmObjectType != null;
        String ourClassName = Complaint.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }
}
