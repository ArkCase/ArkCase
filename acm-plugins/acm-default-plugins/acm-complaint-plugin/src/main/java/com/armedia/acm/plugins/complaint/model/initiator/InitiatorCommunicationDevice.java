/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.initiator;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class InitiatorCommunicationDevice {
	
	private String initiatorDeviceType;
	private List<String> initiatorDeviceTypes;
	private String initiatorDeviceValue;
	private Date initiatorDeviceDate;
	private String initiatorDeviceAddedBy;
	
	/**
	 * @return the initiatorDeviceType
	 */
	public String getInitiatorDeviceType() {
		return initiatorDeviceType;
	}
	
	/**
	 * @param initiatorDeviceType the initiatorDeviceType to set
	 */
	public void setInitiatorDeviceType(String initiatorDeviceType) {
		this.initiatorDeviceType = initiatorDeviceType;
	}
	
	/**
	 * @return the initiatorDeviceTypes
	 */
	public List<String> getInitiatorDeviceTypes() {
		return initiatorDeviceTypes;
	}
	
	/**
	 * @param initiatorDeviceTypes the initiatorDeviceTypes to set
	 */
	public void setInitiatorDeviceTypes(List<String> initiatorDeviceTypes) {
		this.initiatorDeviceTypes = initiatorDeviceTypes;
	}
	
	/**
	 * @return the initiatorDeviceValue
	 */
	public String getInitiatorDeviceValue() {
		return initiatorDeviceValue;
	}
	
	/**
	 * @param initiatorDeviceValue the initiatorDeviceValue to set
	 */
	public void setInitiatorDeviceValue(String initiatorDeviceValue) {
		this.initiatorDeviceValue = initiatorDeviceValue;
	}
	
	/**
	 * @return the initiatorDeviceDate
	 */
	public Date getInitiatorDeviceDate() {
		return initiatorDeviceDate;
	}
	
	/**
	 * @param initiatorDeviceDate the initiatorDeviceDate to set
	 */
	public void setInitiatorDeviceDate(Date initiatorDeviceDate) {
		this.initiatorDeviceDate = initiatorDeviceDate;
	}
	
	/**
	 * @return the initiatorDeviceAddedBy
	 */
	public String getInitiatorDeviceAddedBy() {
		return initiatorDeviceAddedBy;
	}
	
	/**
	 * @param initiatorDeviceAddedBy the initiatorDeviceAddedBy to set
	 */
	public void setInitiatorDeviceAddedBy(String initiatorDeviceAddedBy) {
		this.initiatorDeviceAddedBy = initiatorDeviceAddedBy;
	}
	
}
