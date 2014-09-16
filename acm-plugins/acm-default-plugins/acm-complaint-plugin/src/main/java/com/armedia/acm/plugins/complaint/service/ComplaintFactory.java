package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import java.util.Calendar;
import java.util.Date;

public class ComplaintFactory
{
    public Complaint asAcmComplaint(com.armedia.acm.plugins.complaint.model.complaint.Complaint formComplaint)
    {
        Complaint retval = new Complaint();
        
        retval.setDetails(formComplaint.getComplaintDescription());
        retval.setIncidentDate(formComplaint.getDate());
        retval.setPriority(formComplaint.getPriority());
        retval.setComplaintTitle(formComplaint.getComplaintTitle());
        
        Calendar  cal = Calendar.getInstance();
        cal.setTime(formComplaint.getDate());
        cal.add(Calendar.DATE, 3);
        
        Date dueDate = cal.getTime();        
        retval.setDueDate(dueDate);
        
        if ( formComplaint.getInitiator() != null )
        {
            PersonAssociation pa = new PersonAssociation();
            Person p = new Person();
            pa.setPerson(p);
            retval.setOriginator(pa);

            populatePerson(formComplaint.getInitiator(), pa, p);
        }

        if ( formComplaint.getPeople() != null )
        {
            for ( Contact person : formComplaint.getPeople() )
            {
                PersonAssociation pa = new PersonAssociation();
                Person p = new Person();
                pa.setPerson(p);
                retval.getPersonAssociations().add(pa);

                populatePerson(person, pa, p);

            }
        }

        return retval;
    }

    private void populatePerson(Contact contact, PersonAssociation pa, Person p)
    {
        pa.setPersonDescription(contact.getMainInformation().getDescription());
        pa.setPersonType(contact.getMainInformation().getType());
        p.setTitle(contact.getMainInformation().getTitle());
        p.setGivenName(contact.getMainInformation().getFirstName());
        p.setFamilyName(contact.getMainInformation().getLastName());

        if ( contact.getAlias() != null )
        {
            p.getPersonAliases().add(contact.getAlias());
        }

        if ( contact.getLocation() != null && ! contact.getLocation().isEmpty() )
        {
            p.getAddresses().addAll(contact.getLocation());
        }

        if ( contact.getOrganization() != null && ! contact.getOrganization().isEmpty() )
        {
            p.getOrganizations().addAll(contact.getOrganization());
        }

        if ( contact.getCommunicationDevice() != null && ! contact.getCommunicationDevice().isEmpty() )
        {
            p.getContactMethods().addAll(contact.getCommunicationDevice());
        }
    }
}
