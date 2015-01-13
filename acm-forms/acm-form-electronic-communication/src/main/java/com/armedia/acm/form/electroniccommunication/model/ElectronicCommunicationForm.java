/**
 * 
 */
package com.armedia.acm.form.electroniccommunication.model;

/**
 * @author riste.tutureski
 *
 */
public class ElectronicCommunicationForm {

	private ElectronicCommunicationInformation information;
	private ElectronicCommunicationDetails details;
	
	public ElectronicCommunicationInformation getInformation() {
		return information;
	}
	
	public void setInformation(ElectronicCommunicationInformation information) {
		this.information = information;
	}
	
	public ElectronicCommunicationDetails getDetails() {
		return details;
	}
	
	public void setDetails(ElectronicCommunicationDetails details) {
		this.details = details;
	}
	
}
