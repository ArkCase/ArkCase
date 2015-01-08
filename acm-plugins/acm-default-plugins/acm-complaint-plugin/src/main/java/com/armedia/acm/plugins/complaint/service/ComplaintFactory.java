package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.ParticipantItem;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComplaintFactory
{
	private PersonDao personDao;
	
    public Complaint asAcmComplaint(com.armedia.acm.plugins.complaint.model.complaint.Complaint formComplaint)
    {
        Complaint retval = new Complaint();
        
        retval.setDetails(formComplaint.getComplaintDescription());
        retval.setIncidentDate(formComplaint.getDate());
        retval.setPriority(formComplaint.getPriority());
        retval.setComplaintTitle(formComplaint.getComplaintTitle());
        retval.setParticipants(convertToAcmParticipants(formComplaint.getParticipants()));
        
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

            if (null != formComplaint.getInitiator().getContactType() && "existingInitiator".equals(formComplaint.getInitiator().getContactType()))
            {
            	if (null != formComplaint.getInitiator().getSearchResult() && null != formComplaint.getInitiator().getSearchResult().getId())
            	{
            		p = getPersonDao().find(formComplaint.getInitiator().getSearchResult().getId());
            	}
            }

            pa.setPerson(p);
            retval.setOriginator(pa);

            populatePerson(formComplaint.getInitiator(), pa, p);
        }

        if ( formComplaint.getPeople() != null )
        {
            for ( Contact person : formComplaint.getPeople() )
            {
            	if (person.getMainInformation() != null && person.getMainInformation().getFirstName() != null && person.getMainInformation().getLastName() != null){
	                PersonAssociation pa = new PersonAssociation();
	                Person p = new Person();
	                pa.setPerson(p);
	                retval.getPersonAssociations().add(pa);
	
	                populatePerson(person, pa, p);
	            }
            }
        }

        return retval;
    }

    private void populatePerson(Contact contact, PersonAssociation pa, Person p)
    {
        pa.setPersonDescription(contact.getMainInformation().getDescription());
        pa.setPersonType(contact.getMainInformation().getType());
        pa.setNotes(contact.getNotes());
        
        if (null == p.getId())
        {
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


        if ( "true".equalsIgnoreCase(contact.getMainInformation().getAnonimuos()) )
        {
            pa.getTags().add("Anonymous");
        }
    }
    
    private List<AcmParticipant> convertToAcmParticipants(List<ParticipantItem> items){
    	List<AcmParticipant> participants = new ArrayList<AcmParticipant>();
    	
    	if (items != null && items.size() > 0){
    		for (ParticipantItem item : items){
    			AcmParticipant participant = new AcmParticipant();
    			
    			participant.setObjectType(FrevvoFormName.COMPLAINT.toUpperCase());
    			participant.setParticipantLdapId(item.getValue());
				participant.setParticipantType(item.getType());
				
				participants.add(participant);
    		}
    	}
    	
    	return participants;
    }
    

	/**
	 * @return the personDao
	 */
	public PersonDao getPersonDao() {
		return personDao;
	}

	/**
	 * @param personDao the personDao to set
	 */
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
}
