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
public class PeopleCommunicationDevice {
	
	private String peopleDeviceType;
	private List<String> peopleDeviceTypes;
	private String peopleDeviceValue;
	private Date peopleDeviceDate;
	private String peopleDeviceAddedBy;
	
	/**
	 * @return the peopleDeviceType
	 */
	public String getPeopleDeviceType() {
		return peopleDeviceType;
	}
	
	/**
	 * @param peopleDeviceType the peopleDeviceType to set
	 */
	public void setPeopleDeviceType(String peopleDeviceType) {
		this.peopleDeviceType = peopleDeviceType;
	}
	
	/**
	 * @return the peopleDeviceTypes
	 */
	public List<String> getPeopleDeviceTypes() {
		return peopleDeviceTypes;
	}
	
	/**
	 * @param peopleDeviceTypes the peopleDeviceTypes to set
	 */
	public void setPeopleDeviceTypes(List<String> peopleDeviceTypes) {
		this.peopleDeviceTypes = peopleDeviceTypes;
	}
	
	/**
	 * @return the peopleDeviceValue
	 */
	public String getPeopleDeviceValue() {
		return peopleDeviceValue;
	}
	
	/**
	 * @param peopleDeviceValue the peopleDeviceValue to set
	 */
	public void setPeopleDeviceValue(String peopleDeviceValue) {
		this.peopleDeviceValue = peopleDeviceValue;
	}
	
	/**
	 * @return the peopleDeviceDate
	 */
	public Date getPeopleDeviceDate() {
		return peopleDeviceDate;
	}
	
	/**
	 * @param peopleDeviceDate the peopleDeviceDate to set
	 */
	public void setPeopleDeviceDate(Date peopleDeviceDate) {
		this.peopleDeviceDate = peopleDeviceDate;
	}
	
	/**
	 * @return the peopleDeviceAddedBy
	 */
	public String getPeopleDeviceAddedBy() {
		return peopleDeviceAddedBy;
	}
	
	/**
	 * @param peopleDeviceAddedBy the peopleDeviceAddedBy to set
	 */
	public void setPeopleDeviceAddedBy(String peopleDeviceAddedBy) {
		this.peopleDeviceAddedBy = peopleDeviceAddedBy;
	}
	
}
