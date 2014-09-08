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
public class PeopleAliasInformation {

	private String peopleAliasType;
	private List<String> peopleAliasTypes;
	private String peopleAliasValue;
	private Date peopleAliasDate;
	private String peopleAliasAddedBy;
	
	/**
	 * @return the peopleAliasType
	 */
	public String getPeopleAliasType() {
		return peopleAliasType;
	}
	
	/**
	 * @param peopleAliasType the peopleAliasType to set
	 */
	public void setPeopleAliasType(String peopleAliasType) {
		this.peopleAliasType = peopleAliasType;
	}
	
	/**
	 * @return the peopleAliasTypes
	 */
	public List<String> getPeopleAliasTypes() {
		return peopleAliasTypes;
	}
	
	/**
	 * @param peopleAliasTypes the peopleAliasTypes to set
	 */
	public void setPeopleAliasTypes(List<String> peopleAliasTypes) {
		this.peopleAliasTypes = peopleAliasTypes;
	}
	
	/**
	 * @return the peopleAliasValue
	 */
	public String getPeopleAliasValue() {
		return peopleAliasValue;
	}
	
	/**
	 * @param peopleAliasValue the peopleAliasValue to set
	 */
	public void setPeopleAliasValue(String peopleAliasValue) {
		this.peopleAliasValue = peopleAliasValue;
	}
	
	/**
	 * @return the peopleAliasDate
	 */
	public Date getPeopleAliasDate() {
		return peopleAliasDate;
	}

	/**
	 * @param peopleAliasDate the peopleAliasDate to set
	 */
	public void setPeopleAliasDate(Date peopleAliasDate) {
		this.peopleAliasDate = peopleAliasDate;
	}

	/**
	 * @return the peopleAliasAddedBy
	 */
	public String getPeopleAliasAddedBy() {
		return peopleAliasAddedBy;
	}
	
	/**
	 * @param peopleAliasAddedBy the peopleAliasAddedBy to set
	 */
	public void setPeopleAliasAddedBy(String peopleAliasAddedBy) {
		this.peopleAliasAddedBy = peopleAliasAddedBy;
	}
	
}
