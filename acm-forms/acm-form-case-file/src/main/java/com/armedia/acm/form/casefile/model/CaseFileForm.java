/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.Strings;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.xml.InitiatorPerson;
import com.armedia.acm.plugins.person.model.xml.PeoplePerson;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.CASE_FILE, namespace=FrevvoFormNamespace.CASE_FILE_NAMESPACE)
public class CaseFileForm {

	private Long id;
	private String caseTitle;
	private String caseType;
	private List<String> caseTypes;
	private String caseNumber;
	private String caseDescription;
	private String cmisFolderId;
	private List<ParticipantItem> participants;
	private Map<String, Map<String, Strings>> participantsOptions;
	private List<String> participantsTypeOptions;
	private Person initiator;
	private List<Person> people;
	
	@XmlElement(name="caseId")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlElement(name="caseTitle")
	public String getCaseTitle() {
		return caseTitle;
	}
	
	public void setCaseTitle(String caseTitle) {
		this.caseTitle = caseTitle;
	}
	
	@XmlElement(name="caseType")
	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}
	
	@XmlTransient
	public List<String> getCaseTypes() {
		return caseTypes;
	}

	public void setCaseTypes(List<String> caseTypes) {
		this.caseTypes = caseTypes;
	}

	@XmlTransient
	public String getCaseNumber() {
		return caseNumber;
	}

	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}

	@XmlElement(name="caseDescription")
	public String getCaseDescription() {
		return caseDescription;
	}
	
	public void setCaseDescription(String caseDescription) {
		this.caseDescription = caseDescription;
	}

	@XmlTransient
	public String getCmisFolderId() {
		return cmisFolderId;
	}

	public void setCmisFolderId(String cmisFolderId) {
		this.cmisFolderId = cmisFolderId;
	}

	@XmlElement(name="participantsItem", type=ParticipantItem.class)
	public List<ParticipantItem> getParticipants() {
		return participants;
	}

	public void setParticipants(List<ParticipantItem> participants) {
		this.participants = participants;
	}

	@XmlTransient
	public Map<String, Map<String, Strings>> getParticipantsOptions() {
		return participantsOptions;
	}

	public void setParticipantsOptions(Map<String, Map<String, Strings>> participantsOptions) {
		this.participantsOptions = participantsOptions;
	}

	@XmlTransient
	public List<String> getParticipantsTypeOptions() {
		return participantsTypeOptions;
	}

	public void setParticipantsTypeOptions(List<String> participantsTypeOptions) {
		this.participantsTypeOptions = participantsTypeOptions;
	}

	@XmlElement(name="initiator", type=InitiatorPerson.class)
	public Person getInitiator() {
		return initiator;
	}

	public void setInitiator(Person initiator) {
		this.initiator = initiator;
	}

	@XmlElement(name="people", type=PeoplePerson.class)
	public List<Person> getPeople() {
		return people;
	}

	public void setPeople(List<Person> people) {
		this.people = people;
	}
	
}
