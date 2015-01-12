package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
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

        solr.setId(in.getComplaintId() + "-COMPLAINT");
        solr.setObject_id_s(in.getComplaintId() + "");
        solr.setObject_type_s("COMPLAINT");
        solr.setTitle_parseable(in.getComplaintTitle());
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

        AcmUser assignee = findAssignee(assigneeUserId);

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

        // all protected objects must have protected_object_b
        solr.setProtected_object_b(true);

        boolean publicDoc = everyoneHasRead(in);
        solr.setPublic_doc_b(publicDoc);

        if ( !publicDoc )
        {
            List<String> readers = getReaders(in);
            solr.setAllow_acl_ss(readers);
        }

        List<String> denied = getDenied(in);
        solr.setDeny_acl_ss(denied);

        solr.setName(in.getComplaintNumber());
        solr.setObject_id_s(in.getComplaintId() + "");
        solr.setObject_type_s("COMPLAINT");
        solr.setId(in.getComplaintId() + "-COMPLAINT");

        solr.setAuthor(in.getCreator());
        solr.setCreate_dt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified(in.getModified());

        solr.setTitle_t(in.getComplaintTitle());
        solr.setStatus_s(in.getStatus());

        String assigneeUserId = findAssigneeUserId(in);
        solr.setAssignee_s(assigneeUserId);

        return solr;
    }

    private List<String> getDenied(AcmAssignedObject in)
    {
        return getReadersWithLevel("mandatory deny", in);
    }

    private List<String> getReaders(AcmAssignedObject in)
    {
        return getReadersWithLevel("grant", in);
    }

    private List<String> getReadersWithLevel(String level, AcmAssignedObject in)
    {
        List<String> readers = new ArrayList<>();
        for ( AcmParticipant ap : in.getParticipants() )
        {
            for ( AcmParticipantPrivilege priv : ap.getPrivileges() )
            {
                if ("read".equals(priv.getObjectAction()) && level.equals(priv.getAccessType()) )
                {
                    readers.add(ap.getParticipantLdapId());
                    break;
                }
            }
        }

        return readers;
    }

    private boolean everyoneHasRead(Complaint in)
    {
        for ( AcmParticipant ap : in.getParticipants() )
        {
            if ( "*".equals(ap.getParticipantLdapId()) )
            {
                for ( AcmParticipantPrivilege priv : ap.getPrivileges() )
                {
                    if ( "read".equals(priv.getObjectAction()) && "grant".equals(priv.getAccessType()) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private AcmUser findAssignee(String assigneeUserId)
    {
        if ( assigneeUserId == null || assigneeUserId.trim().isEmpty() )
        {
            return null;
        }

        try
        {
            AcmUser user = getUserDao().findByUserId(assigneeUserId);
            if (user != null)
            {
                return user;
            }
        }
        catch (PersistenceException pe)
        {
            log.error("Could not find user record: " + pe.getMessage(), pe);
        }


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
}
