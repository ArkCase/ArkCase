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
public class PeopleLocationInformation {

	private String peopleLocationType;
	private List<String> peopleLocationTypes;
	private String peopleLocationAddress;
	private String peopleLocationCity;
	private String peopleLocationState;
	private String peopleLocationZip;
	private Date peopleLocationDate;
	private String peopleLocationAddedBy;
	
	/**
	 * @return the peopleLocationType
	 */
	public String getPeopleLocationType() {
		return peopleLocationType;
	}
	
	/**
	 * @param peopleLocationType the peopleLocationType to set
	 */
	public void setPeopleLocationType(String peopleLocationType) {
		this.peopleLocationType = peopleLocationType;
	}
	
	/**
	 * @return the peopleLocationTypes
	 */
	public List<String> getPeopleLocationTypes() {
		return peopleLocationTypes;
	}
	
	/**
	 * @param peopleLocationTypes the peopleLocationTypes to set
	 */
	public void setPeopleLocationTypes(List<String> peopleLocationTypes) {
		this.peopleLocationTypes = peopleLocationTypes;
	}
	
	/**
	 * @return the peopleLocationAddress
	 */
	public String getPeopleLocationAddress() {
		return peopleLocationAddress;
	}
	
	/**
	 * @param peopleLocationAddress the peopleLocationAddress to set
	 */
	public void setPeopleLocationAddress(String peopleLocationAddress) {
		this.peopleLocationAddress = peopleLocationAddress;
	}
	
	/**
	 * @return the peopleLocationCity
	 */
	public String getPeopleLocationCity() {
		return peopleLocationCity;
	}
	
	/**
	 * @param peopleLocationCity the peopleLocationCity to set
	 */
	public void setPeopleLocationCity(String peopleLocationCity) {
		this.peopleLocationCity = peopleLocationCity;
	}
	
	/**
	 * @return the peopleLocationState
	 */
	public String getPeopleLocationState() {
		return peopleLocationState;
	}
	
	/**
	 * @param peopleLocationState the peopleLocationState to set
	 */
	public void setPeopleLocationState(String peopleLocationState) {
		this.peopleLocationState = peopleLocationState;
	}
	
	/**
	 * @return the peopleLocationZip
	 */
	public String getPeopleLocationZip() {
		return peopleLocationZip;
	}
	
	/**
	 * @param peopleLocationZip the peopleLocationZip to set
	 */
	public void setPeopleLocationZip(String peopleLocationZip) {
		this.peopleLocationZip = peopleLocationZip;
	}
	
	/**
	 * @return the peopleLocationDate
	 */
	public Date getPeopleLocationDate() {
		return peopleLocationDate;
	}

	/**
	 * @param peopleLocationDate the peopleLocationDate to set
	 */
	public void setPeopleLocationDate(Date peopleLocationDate) {
		this.peopleLocationDate = peopleLocationDate;
	}

	/**
	 * @return the peopleLocationAddedBy
	 */
	public String getPeopleLocationAddedBy() {
		return peopleLocationAddedBy;
	}
	
	/**
	 * @param peopleLocationAddedBy the peopleLocationAddedBy to set
	 */
	public void setPeopleLocationAddedBy(String peopleLocationAddedBy) {
		this.peopleLocationAddedBy = peopleLocationAddedBy;
	}
	
}
