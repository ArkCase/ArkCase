package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.Item;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComplaintFactory
{
    public Complaint asAcmComplaint(com.armedia.acm.plugins.complaint.model.complaint.Complaint formComplaint)
    {
        Complaint retval = new Complaint();
        
        retval.setDetails(formComplaint.getComplaintDescription());
        retval.setIncidentDate(formComplaint.getDate());
        retval.setPriority(formComplaint.getPriority());
        retval.setComplaintTitle(formComplaint.getComplaintTitle());
        retval.setApprovers(convertItemsToList(formComplaint.getOwners()));
        
        Calendar  cal = Calendar.getInstance();
        cal.setTime(formComplaint.getDate());
        cal.add(Calendar.DATE, 3);
        
        Date dueDate = cal.getTime();        
        retval.setDueDate(dueDate);
        retval.setComplaintType(formComplaint.getCategory());
        retval.setTag(formComplaint.getComplaintTag());
        retval.setFrequency(formComplaint.getFrequency());
        retval.setLocation(formComplaint.getLocation());
        
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
        pa.setNotes(contact.getNotes());
        
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


        if ( "true".equalsIgnoreCase(contact.getMainInformation().getAnonimuos()) )
        {
            p.getSecurityTags().add("Anonymous");
        }
    }
    
    private List<String> convertItemsToList(List<Item> items){
    	List<String> itemsString = new ArrayList<String>();
    	
    	if (items != null && items.size() > 0){
    		for (int i = 0; i < items.size(); i++) {
    			itemsString.add(items.get(i).getValue());
    		}
    	}else{
    		return null;
    	}
    	
    	return itemsString;
    }
}
