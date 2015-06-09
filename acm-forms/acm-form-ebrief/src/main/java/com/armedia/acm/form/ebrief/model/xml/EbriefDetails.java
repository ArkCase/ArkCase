/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author riste.tutureski
 *
 */
public class EbriefDetails {

	private Long assignedToId;
	private String assignedToUserId;
	private String assignedTo;
	private Date nextCourtHearingDate;
	private String courtLocation;
	private List<String> courtLocations;
	
	@XmlElement(name="assignedToId")
	public Long getAssignedToId() {
		return assignedToId;
	}

	public void setAssignedToId(Long assignedToId) {
		this.assignedToId = assignedToId;
	}

	@XmlElement(name="assignedToUserId")
	public String getAssignedToUserId() {
		return assignedToUserId;
	}

	public void setAssignedToUserId(String assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}

	@XmlElement(name="assignedTo")
	public String getAssignedTo() {
		return assignedTo;
	}
	
	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	@XmlElement(name="nextCourtHearingDate")
	public Date getNextCourtHearingDate() {
		return nextCourtHearingDate;
	}
	
	public void setNextCourtHearingDate(Date nextCourtHearingDate) {
		this.nextCourtHearingDate = nextCourtHearingDate;
	}
	
	@XmlElement(name="courtLocation")
	public String getCourtLocation() {
		return courtLocation;
	}
	
	public void setCourtLocation(String courtLocation) {
		this.courtLocation = courtLocation;
	}

	@XmlTransient
	public List<String> getCourtLocations() {
		return courtLocations;
	}

	public void setCourtLocations(List<String> courtLocations) {
		this.courtLocations = courtLocations;
	}	
	
}
