/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.initiator;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class Initiator {

	private InitiatorMainInformation initiatorMainInformation;
	private List<InitiatorCommunicationDevice> initiatorCommunicationDevice;
	private List<InitiatorOrganizationInformation> initiatorOrganizationInformation;
	private List<InitiatorLocationInformation> initiatorLocationInformation;
	private List<InitiatorAliasInformation> initiatorAliasInformation;
	private String initiatorNotes;
	
	/**
	 * @return the initiatorMainInformation
	 */
	public InitiatorMainInformation getInitiatorMainInformation() {
		return initiatorMainInformation;
	}
	
	/**
	 * @param initiatorMainInformation the initiatorMainInformation to set
	 */
	public void setInitiatorMainInformation(
			InitiatorMainInformation initiatorMainInformation) {
		this.initiatorMainInformation = initiatorMainInformation;
	}
	
	/**
	 * @return the initiatorCommunicationDevice
	 */
	public List<InitiatorCommunicationDevice> getInitiatorCommunicationDevice() {
		return initiatorCommunicationDevice;
	}
	
	/**
	 * @param initiatorCommunicationDevice the initiatorCommunicationDevice to set
	 */
	public void setInitiatorCommunicationDevice(
			List<InitiatorCommunicationDevice> initiatorCommunicationDevice) {
		this.initiatorCommunicationDevice = initiatorCommunicationDevice;
	}
	
	/**
	 * @return the initiatorOrganizationInformation
	 */
	public List<InitiatorOrganizationInformation> getInitiatorOrganizationInformation() {
		return initiatorOrganizationInformation;
	}
	
	/**
	 * @param initiatorOrganizationInformation the initiatorOrganizationInformation to set
	 */
	public void setInitiatorOrganizationInformation(
			List<InitiatorOrganizationInformation> initiatorOrganizationInformation) {
		this.initiatorOrganizationInformation = initiatorOrganizationInformation;
	}
	
	/**
	 * @return the initiatorLocationInformation
	 */
	public List<InitiatorLocationInformation> getInitiatorLocationInformation() {
		return initiatorLocationInformation;
	}
	
	/**
	 * @param initiatorLocationInformation the initiatorLocationInformation to set
	 */
	public void setInitiatorLocationInformation(
			List<InitiatorLocationInformation> initiatorLocationInformation) {
		this.initiatorLocationInformation = initiatorLocationInformation;
	}
	
	/**
	 * @return the initiatorAliasInformation
	 */
	public List<InitiatorAliasInformation> getInitiatorAliasInformation() {
		return initiatorAliasInformation;
	}

	/**
	 * @param initiatorAliasInformation the initiatorAliasInformation to set
	 */
	public void setInitiatorAliasInformation(
			List<InitiatorAliasInformation> initiatorAliasInformation) {
		this.initiatorAliasInformation = initiatorAliasInformation;
	}

	/**
	 * @return the initiatorNotes
	 */
	public String getInitiatorNotes() {
		return initiatorNotes;
	}
	
	/**
	 * @param initiatorNotes the initiatorNotes to set
	 */
	public void setInitiatorNotes(String initiatorNotes) {
		this.initiatorNotes = initiatorNotes;
	}
	
}
