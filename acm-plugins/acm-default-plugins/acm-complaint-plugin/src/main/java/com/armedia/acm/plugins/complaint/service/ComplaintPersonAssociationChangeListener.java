package com.armedia.acm.plugins.complaint.service;


import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociationUpdatedEvent;
import org.springframework.context.ApplicationListener;

public class ComplaintPersonAssociationChangeListener implements ApplicationListener<PersonAssociationUpdatedEvent>
{

    private ComplaintDao complaintDao;
    private ComplaintEventPublisher complaintEventPublisher;

    @Override
    public void onApplicationEvent(PersonAssociationUpdatedEvent event)
    {
        PersonAssociation personAssociation = (PersonAssociation) event.getSource();
        if (personAssociation.getParentType().equals(ComplaintConstants.OBJECT_TYPE))
        {
            Complaint complaint = getComplaintDao().find(personAssociation.getParentId());
            getComplaintEventPublisher().publishComplaintModified(complaint, event.getIpAddress(), "updated");
        }

    }

    public ComplaintEventPublisher getComplaintEventPublisher()
    {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(ComplaintEventPublisher complaintEventPublisher)
    {
        this.complaintEventPublisher = complaintEventPublisher;
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
