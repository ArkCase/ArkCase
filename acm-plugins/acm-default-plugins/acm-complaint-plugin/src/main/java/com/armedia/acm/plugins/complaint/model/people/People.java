/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.people;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class People {

	private PeopleMainInformation peopleMainInformation;
	private List<PeopleCommunicationDevice> peopleCommunicationDevice;
	private List<PeopleOrganizationInformation> peopleOrganizationInformation;
	private List<PeopleLocationInformation> peopleLocationInformation;
	private List<PeopleAliasInformation> peopleAliasInformation;
	private String peopleNotes;
	
	/**
	 * @return the peopleMainInformation
	 */
	public PeopleMainInformation getPeopleMainInformation() {
		return peopleMainInformation;
	}
	
	/**
	 * @param peopleMainInformation the peopleMainInformation to set
	 */
	public void setPeopleMainInformation(
			PeopleMainInformation peopleMainInformation) {
		this.peopleMainInformation = peopleMainInformation;
	}
	
	/**
	 * @return the peopleCommunicationDevice
	 */
	public List<PeopleCommunicationDevice> getPeopleCommunicationDevice() {
		return peopleCommunicationDevice;
	}
	
	/**
	 * @param peopleCommunicationDevice the peopleCommunicationDevice to set
	 */
	public void setPeopleCommunicationDevice(
			List<PeopleCommunicationDevice> peopleCommunicationDevice) {
		this.peopleCommunicationDevice = peopleCommunicationDevice;
	}
	
	/**
	 * @return the peopleOrganizationInformation
	 */
	public List<PeopleOrganizationInformation> getPeopleOrganizationInformation() {
		return peopleOrganizationInformation;
	}
	
	/**
	 * @param peopleOrganizationInformation the peopleOrganizationInformation to set
	 */
	public void setPeopleOrganizationInformation(
			List<PeopleOrganizationInformation> peopleOrganizationInformation) {
		this.peopleOrganizationInformation = peopleOrganizationInformation;
	}
	
	/**
	 * @return the peopleLocationInformation
	 */
	public List<PeopleLocationInformation> getPeopleLocationInformation() {
		return peopleLocationInformation;
	}
	
	/**
	 * @param peopleLocationInformation the peopleLocationInformation to set
	 */
	public void setPeopleLocationInformation(
			List<PeopleLocationInformation> peopleLocationInformation) {
		this.peopleLocationInformation = peopleLocationInformation;
	}
	
	/**
	 * @return the peopleAliasInformation
	 */
	public List<PeopleAliasInformation> getPeopleAliasInformation() {
		return peopleAliasInformation;
	}

	/**
	 * @param peopleAliasInformation the peopleAliasInformation to set
	 */
	public void setPeopleAliasInformation(
			List<PeopleAliasInformation> peopleAliasInformation) {
		this.peopleAliasInformation = peopleAliasInformation;
	}

	/**
	 * @return the peopleNotes
	 */
	public String getPeopleNotes() {
		return peopleNotes;
	}
	
	/**
	 * @param peopleNotes the peopleNotes to set
	 */
	public void setPeopleNotes(String peopleNotes) {
		this.peopleNotes = peopleNotes;
	}
	
}
