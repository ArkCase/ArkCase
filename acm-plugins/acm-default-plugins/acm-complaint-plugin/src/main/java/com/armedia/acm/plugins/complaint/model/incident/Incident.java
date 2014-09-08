/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.incident;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class Incident {
	
	private String incidentCategory;
	private List<String> incidentCategories;
	private String complaintTitle;
	private String complaintDescription;
	private String priority;
	private List<String> priorities;
	private Date incidentDate;
	private String complaintTag;
	private String frequency;
	private List<String> frequencies;
	private String location;
	/**
	 * @return the incidentCategory
	 */
	public String getIncidentCategory() {
		return incidentCategory;
	}
	
	/**
	 * @param incidentCategory the incidentCategory to set
	 */
	public void setIncidentCategory(String incidentCategory) {
		this.incidentCategory = incidentCategory;
	}
	
	/**
	 * @return the incidentCategories
	 */
	public List<String> getIncidentCategories() {
		return incidentCategories;
	}
	
	/**
	 * @param incidentCategories the incidentCategories to set
	 */
	public void setIncidentCategories(List<String> incidentCategories) {
		this.incidentCategories = incidentCategories;
	}
	
	/**
	 * @return the complaintTitle
	 */
	public String getComplaintTitle() {
		return complaintTitle;
	}
	
	/**
	 * @param complaintTitle the complaintTitle to set
	 */
	public void setComplaintTitle(String complaintTitle) {
		this.complaintTitle = complaintTitle;
	}
	
	/**
	 * @return the complaintDescription
	 */
	public String getComplaintDescription() {
		return complaintDescription;
	}
	
	/**
	 * @param complaintDescription the complaintDescription to set
	 */
	public void setComplaintDescription(String complaintDescription) {
		this.complaintDescription = complaintDescription;
	}
	
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	/**
	 * @return the priorities
	 */
	public List<String> getPriorities() {
		return priorities;
	}
	
	/**
	 * @param priorities the priorities to set
	 */
	public void setPriorities(List<String> priorities) {
		this.priorities = priorities;
	}
	
	/**
	 * @return the incidentDate
	 */
	public Date getIncidentDate() {
		return incidentDate;
	}
	
	/**
	 * @param incidentDate the incidentDate to set
	 */
	public void setIncidentDate(Date incidentDate) {
		this.incidentDate = incidentDate;
	}
	
	/**
	 * @return the complaintTag
	 */
	public String getComplaintTag() {
		return complaintTag;
	}
	
	/**
	 * @param complaintTag the complaintTag to set
	 */
	public void setComplaintTag(String complaintTag) {
		this.complaintTag = complaintTag;
	}
	
	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}
	
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
	/**
	 * @return the frequencies
	 */
	public List<String> getFrequencies() {
		return frequencies;
	}
	
	/**
	 * @param frequencies the frequencies to set
	 */
	public void setFrequencies(List<String> frequencies) {
		this.frequencies = frequencies;
	}
	
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
}
