package com.armedia.acm.plugins.complaint.service;


import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

public class ComplaintFactory
{
    public Complaint asAcmComplaint(com.armedia.acm.plugins.complaint.model.complaint.Complaint formComplaint)
    {
        Complaint retval = new Complaint();

        retval.setDetails(formComplaint.getComplaintDescription());
        retval.setIncidentDate(formComplaint.getDate());
        retval.setPriority(formComplaint.getPriority());

        if ( formComplaint.getInitiator() != null )
        {
            PersonAssociation pa = new PersonAssociation();
            Person p = new Person();
            pa.setPerson(p);
            retval.setOriginator(pa);

            pa.setPersonDescription(formComplaint.getInitiator().getMainInformation().getDescription());
            pa.setPersonType(formComplaint.getInitiator().getMainInformation().getType());
            p.setTitle(formComplaint.getInitiator().getMainInformation().getTitle());
            p.setGivenName(formComplaint.getInitiator().getMainInformation().getFirstName());
            p.setFamilyName(formComplaint.getInitiator().getMainInformation().getLastName());
        }

        return retval;
    }
}
