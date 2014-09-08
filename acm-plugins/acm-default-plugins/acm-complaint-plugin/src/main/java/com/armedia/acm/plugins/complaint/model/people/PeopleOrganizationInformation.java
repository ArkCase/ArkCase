/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.people;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class PeopleOrganizationInformation {

	private String peopleOrganizationType;
	private List<String> peopleOrganizationTypes;
	private String peopleOrganizationName;
	private Date peopleOrganizationDate;
	private String peopleOrganizationAddedBy;
	
	/**
	 * @return the peopleOrganizationType
	 */
	public String getPeopleOrganizationType() {
		return peopleOrganizationType;
	}
	
	/**
	 * @param peopleOrganizationType the peopleOrganizationType to set
	 */
	public void setPeopleOrganizationType(String peopleOrganizationType) {
		this.peopleOrganizationType = peopleOrganizationType;
	}
	
	/**
	 * @return the peopleOrganizationTypes
	 */
	public List<String> getPeopleOrganizationTypes() {
		return peopleOrganizationTypes;
	}
	
	/**
	 * @param peopleOrganizationTypes the peopleOrganizationTypes to set
	 */
	public void setPeopleOrganizationTypes(
			List<String> peopleOrganizationTypes) {
		this.peopleOrganizationTypes = peopleOrganizationTypes;
	}
	
	/**
	 * @return the peopleOrganizationName
	 */
	public String getPeopleOrganizationName() {
		return peopleOrganizationName;
	}
	
	/**
	 * @param peopleOrganizationName the peopleOrganizationName to set
	 */
	public void setPeopleOrganizationName(String peopleOrganizationName) {
		this.peopleOrganizationName = peopleOrganizationName;
	}
	
	/**
	 * @return the peopleOrganizationDate
	 */
	public Date getPeopleOrganizationDate() {
		return peopleOrganizationDate;
	}
	
	/**
	 * @param peopleOrganizationDate the peopleOrganizationDate to set
	 */
	public void setPeopleOrganizationDate(Date peopleOrganizationDate) {
		this.peopleOrganizationDate = peopleOrganizationDate;
	}
	
	/**
	 * @return the peopleOrganizationAddedBy
	 */
	public String getPeopleOrganizationAddedBy() {
		return peopleOrganizationAddedBy;
	}
	
	/**
	 * @param peopleOrganizationAddedBy the peopleOrganizationAddedBy to set
	 */
	public void setPeopleOrganizationAddedBy(String peopleOrganizationAddedBy) {
		this.peopleOrganizationAddedBy = peopleOrganizationAddedBy;
	}
	
}
