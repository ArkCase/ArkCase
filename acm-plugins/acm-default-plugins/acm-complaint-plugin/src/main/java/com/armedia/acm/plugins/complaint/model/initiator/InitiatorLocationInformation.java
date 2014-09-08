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
public class InitiatorLocationInformation {

	private String initiatorLocationType;
	private List<String> initiatorLocationTypes;
	private String initiatorLocationAddress;
	private String initiatorLocationCity;
	private String initiatorLocationState;
	private String initiatorLocationZip;
	private Date initiatorLocationDate;
	private String initiatorLocationAddedBy;
	
	/**
	 * @return the initiatorLocationType
	 */
	public String getInitiatorLocationType() {
		return initiatorLocationType;
	}
	
	/**
	 * @param initiatorLocationType the initiatorLocationType to set
	 */
	public void setInitiatorLocationType(String initiatorLocationType) {
		this.initiatorLocationType = initiatorLocationType;
	}
	
	/**
	 * @return the initiatorLocationTypes
	 */
	public List<String> getInitiatorLocationTypes() {
		return initiatorLocationTypes;
	}
	
	/**
	 * @param initiatorLocationTypes the initiatorLocationTypes to set
	 */
	public void setInitiatorLocationTypes(List<String> initiatorLocationTypes) {
		this.initiatorLocationTypes = initiatorLocationTypes;
	}
	
	/**
	 * @return the initiatorLocationAddress
	 */
	public String getInitiatorLocationAddress() {
		return initiatorLocationAddress;
	}
	
	/**
	 * @param initiatorLocationAddress the initiatorLocationAddress to set
	 */
	public void setInitiatorLocationAddress(String initiatorLocationAddress) {
		this.initiatorLocationAddress = initiatorLocationAddress;
	}
	
	/**
	 * @return the initiatorLocationCity
	 */
	public String getInitiatorLocationCity() {
		return initiatorLocationCity;
	}
	
	/**
	 * @param initiatorLocationCity the initiatorLocationCity to set
	 */
	public void setInitiatorLocationCity(String initiatorLocationCity) {
		this.initiatorLocationCity = initiatorLocationCity;
	}
	
	/**
	 * @return the initiatorLocationState
	 */
	public String getInitiatorLocationState() {
		return initiatorLocationState;
	}
	
	/**
	 * @param initiatorLocationState the initiatorLocationState to set
	 */
	public void setInitiatorLocationState(String initiatorLocationState) {
		this.initiatorLocationState = initiatorLocationState;
	}
	
	/**
	 * @return the initiatorLocationZip
	 */
	public String getInitiatorLocationZip() {
		return initiatorLocationZip;
	}
	
	/**
	 * @param initiatorLocationZip the initiatorLocationZip to set
	 */
	public void setInitiatorLocationZip(String initiatorLocationZip) {
		this.initiatorLocationZip = initiatorLocationZip;
	}
	
	/**
	 * @return the initiatorLocationDate
	 */
	public Date getInitiatorLocationDate() {
		return initiatorLocationDate;
	}

	/**
	 * @param initiatorLocationDate the initiatorLocationDate to set
	 */
	public void setInitiatorLocationDate(Date initiatorLocationDate) {
		this.initiatorLocationDate = initiatorLocationDate;
	}

	/**
	 * @return the initiatorLocationAddedBy
	 */
	public String getInitiatorLocationAddedBy() {
		return initiatorLocationAddedBy;
	}
	
	/**
	 * @param initiatorLocationAddedBy the initiatorLocationAddedBy to set
	 */
	public void setInitiatorLocationAddedBy(String initiatorLocationAddedBy) {
		this.initiatorLocationAddedBy = initiatorLocationAddedBy;
	}
	
}
