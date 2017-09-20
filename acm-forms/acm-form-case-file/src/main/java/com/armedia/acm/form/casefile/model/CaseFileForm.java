/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.form.config.xml.PeopleItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
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
	private List<String> participantsTypeOptions;
	private Map<String, String> participantsPrivilegeTypes;
	private OwningGroupItem owningGroup;
	private List<String> owningGroupOptions;
	private Long initiatorId;
	private String initiatorFullName;
	private String personType;
	private List<PeopleItem> people;
	
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
	public List<String> getParticipantsTypeOptions() {
		return participantsTypeOptions;
	}

	public void setParticipantsTypeOptions(List<String> participantsTypeOptions) {
		this.participantsTypeOptions = participantsTypeOptions;
	}

	@XmlTransient
	public Map<String, String> getParticipantsPrivilegeTypes() {
		return participantsPrivilegeTypes;
	}

	public void setParticipantsPrivilegeTypes(
			Map<String, String> participantsPrivilegeTypes) {
		this.participantsPrivilegeTypes = participantsPrivilegeTypes;
	}

	@XmlElement(name="owningGroup")
	public OwningGroupItem getOwningGroup() {
		return owningGroup;
	}

	public void setOwningGroup(OwningGroupItem owningGroup) {
		this.owningGroup = owningGroup;
	}

	@XmlTransient
	public List<String> getOwningGroupOptions() {
		return owningGroupOptions;
	}

	public void setOwningGroupOptions(List<String> owningGroupOptions) {
		this.owningGroupOptions = owningGroupOptions;
	}

	@XmlElement(name="initiatorId")
	public Long getInitiatorId()
	{
		return initiatorId;
	}

	public void setInitiatorId(Long initiatorId)
	{
		this.initiatorId = initiatorId;
	}

	@XmlElement(name="initiatorFullName")
	public String getInitiatorFullName()
	{
		return initiatorFullName;
	}

	public void setInitiatorFullName(String initiatorFullName)
	{
		this.initiatorFullName = initiatorFullName;
	}

	@XmlElement(name="personType")
	public String getPersonType()
	{
		return personType;
	}

	public void setPersonType(String personType)
	{
		this.personType = personType;
	}

	@XmlElement(name="peopleItem", type=PeopleItem.class)
	public List<PeopleItem> getPeople()
	{
		return people;
	}

	public void setPeople(List<PeopleItem> people)
	{
		this.people = people;
	}
}
