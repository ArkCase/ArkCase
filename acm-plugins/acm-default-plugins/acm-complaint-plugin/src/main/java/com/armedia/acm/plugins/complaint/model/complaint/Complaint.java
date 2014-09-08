/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

import java.util.List;

import com.armedia.acm.plugins.complaint.model.incident.Incident;
import com.armedia.acm.plugins.complaint.model.initiator.Initiator;
import com.armedia.acm.plugins.complaint.model.people.People;

/**
 * @author riste.tutureski
 *
 */
public class Complaint {
	
	private Long complaintId;
	private Initiator initiator;
	private Incident incident;
	private List<People> people;
	
	/**
	 * @return the complaintId
	 */
	public Long getComplaintId() {
		return complaintId;
	}

	/**
	 * @param complaintId the complaintId to set
	 */
	public void setComplaintId(Long complaintId) {
		this.complaintId = complaintId;
	}

	/**
	 * @return the initiator
	 */
	public Initiator getInitiator() {
		return initiator;
	}
	
	/**
	 * @param initiator the initiator to set
	 */
	public void setInitiator(Initiator initiator) {
		this.initiator = initiator;
	}
	
	/**
	 * @return the incident
	 */
	public Incident getIncident() {
		return incident;
	}
	
	/**
	 * @param incident the incident to set
	 */
	public void setIncident(Incident incident) {
		this.incident = incident;
	}
	
	/**
	 * @return the people
	 */
	public List<People> getPeople() {
		return people;
	}
	
	/**
	 * @param people the people to set
	 */
	public void setPeople(List<People> people) {
		this.people = people;
	}
	
}
