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
public class InitiatorAliasInformation {

	private String initiatorAliasType;
	private List<String> initiatorAliasTypes;
	private String initiatorAliasValue;
	private Date initiatorAliasDate;
	private String initiatorAliasAddedBy;
	
	/**
	 * @return the initiatorAliasType
	 */
	public String getInitiatorAliasType() {
		return initiatorAliasType;
	}
	
	/**
	 * @param initiatorAliasType the initiatorAliasType to set
	 */
	public void setInitiatorAliasType(String initiatorAliasType) {
		this.initiatorAliasType = initiatorAliasType;
	}
	
	/**
	 * @return the initiatorAliasTypes
	 */
	public List<String> getInitiatorAliasTypes() {
		return initiatorAliasTypes;
	}
	
	/**
	 * @param initiatorAliasTypes the initiatorAliasTypes to set
	 */
	public void setInitiatorAliasTypes(List<String> initiatorAliasTypes) {
		this.initiatorAliasTypes = initiatorAliasTypes;
	}
	
	/**
	 * @return the initiatorAliasValue
	 */
	public String getInitiatorAliasValue() {
		return initiatorAliasValue;
	}
	
	/**
	 * @param initiatorAliasValue the initiatorAliasValue to set
	 */
	public void setInitiatorAliasValue(String initiatorAliasValue) {
		this.initiatorAliasValue = initiatorAliasValue;
	}
	
	/**
	 * @return the initiatorAliasDate
	 */
	public Date getInitiatorAliasDate() {
		return initiatorAliasDate;
	}

	/**
	 * @param initiatorAliasDate the initiatorAliasDate to set
	 */
	public void setInitiatorAliasDate(Date initiatorAliasDate) {
		this.initiatorAliasDate = initiatorAliasDate;
	}

	/**
	 * @return the initiatorAliasAddedBy
	 */
	public String getInitiatorAliasAddedBy() {
		return initiatorAliasAddedBy;
	}
	
	/**
	 * @param initiatorAliasAddedBy the initiatorAliasAddedBy to set
	 */
	public void setInitiatorAliasAddedBy(String initiatorAliasAddedBy) {
		this.initiatorAliasAddedBy = initiatorAliasAddedBy;
	}
	
}
