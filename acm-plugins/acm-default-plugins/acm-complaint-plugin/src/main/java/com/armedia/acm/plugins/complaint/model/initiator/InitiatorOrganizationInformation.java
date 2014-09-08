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
public class InitiatorOrganizationInformation {

	private String initiatorOrganizationType;
	private List<String> initiatorOrganizationTypes;
	private String initiatorOrganizationName;
	private Date initiatorOrganizationDate;
	private String initiatorOrganizationAddedBy;
	
	/**
	 * @return the initiatorOrganizationType
	 */
	public String getInitiatorOrganizationType() {
		return initiatorOrganizationType;
	}
	
	/**
	 * @param initiatorOrganizationType the initiatorOrganizationType to set
	 */
	public void setInitiatorOrganizationType(String initiatorOrganizationType) {
		this.initiatorOrganizationType = initiatorOrganizationType;
	}
	
	/**
	 * @return the initiatorOrganizationTypes
	 */
	public List<String> getInitiatorOrganizationTypes() {
		return initiatorOrganizationTypes;
	}
	
	/**
	 * @param initiatorOrganizationTypes the initiatorOrganizationTypes to set
	 */
	public void setInitiatorOrganizationTypes(
			List<String> initiatorOrganizationTypes) {
		this.initiatorOrganizationTypes = initiatorOrganizationTypes;
	}
	
	/**
	 * @return the initiatorOrganizationName
	 */
	public String getInitiatorOrganizationName() {
		return initiatorOrganizationName;
	}
	
	/**
	 * @param initiatorOrganizationName the initiatorOrganizationName to set
	 */
	public void setInitiatorOrganizationName(String initiatorOrganizationName) {
		this.initiatorOrganizationName = initiatorOrganizationName;
	}
	
	/**
	 * @return the initiatorOrganizationDate
	 */
	public Date getInitiatorOrganizationDate() {
		return initiatorOrganizationDate;
	}
	
	/**
	 * @param initiatorOrganizationDate the initiatorOrganizationDate to set
	 */
	public void setInitiatorOrganizationDate(Date initiatorOrganizationDate) {
		this.initiatorOrganizationDate = initiatorOrganizationDate;
	}
	
	/**
	 * @return the initiatorOrganizationAddedBy
	 */
	public String getInitiatorOrganizationAddedBy() {
		return initiatorOrganizationAddedBy;
	}
	
	/**
	 * @param initiatorOrganizationAddedBy the initiatorOrganizationAddedBy to set
	 */
	public void setInitiatorOrganizationAddedBy(String initiatorOrganizationAddedBy) {
		this.initiatorOrganizationAddedBy = initiatorOrganizationAddedBy;
	}
	
}
