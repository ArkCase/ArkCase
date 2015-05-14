/**
 * 
 */
package com.armedia.acm.form.ebrief.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.form.ebrief.model.xml.BoxOneSection;
import com.armedia.acm.form.ebrief.model.xml.CertificateSection;
import com.armedia.acm.form.ebrief.model.xml.CourtSection;
import com.armedia.acm.form.ebrief.model.xml.OffenceSection;
import com.armedia.acm.form.ebrief.model.xml.PropertySection;
import com.armedia.acm.form.ebrief.model.xml.ProsecutorSection;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.xml.DefendantPerson;
import com.armedia.acm.plugins.person.model.xml.OfficerPerson;
import com.armedia.acm.plugins.person.model.xml.PoliceWitnessPerson;
import com.armedia.acm.plugins.person.model.xml.VictimPerson;
import com.armedia.acm.plugins.person.model.xml.WitnessVictimPerson;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.EBRIEF, namespace=FrevvoFormNamespace.EBRIEF_NAMESPACE)
public class EbriefForm extends FrevvoForm{

	private Long id;
	private String type;
	private List<String> types;
	private List<BoxOneSection> boxOneSections;
	private String notes;
	private CertificateSection certificateSection;
	private PropertySection propertySection;
	private List<Person> witnessVictims;
	private String refersToOperation;
	private Person defendant;
	private OffenceSection offenceSection;
	private ProsecutorSection prosecutorSection;
	private CourtSection courtSection;
	private Person victim;
	private Person officer;
	private List<Person> policeWinesses;
	private String trialDates;
	private String trialReason;
	private String cmisFolderId;
	private List<ParticipantItem> participants;
	private List<String> participantsTypeOptions;
	private Map<String, String> participantsPrivilegeTypes;
	private OwningGroupItem owningGroup;
	private List<String> owningGroupOptions;
	
	@XmlElement(name="eBriefId")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(name="eBriefType")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlTransient
	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	@XmlElement(name="boxOneSection")
	public List<BoxOneSection> getBoxOneSections() {
		return boxOneSections;
	}

	public void setBoxOneSections(List<BoxOneSection> boxOneSections) {
		this.boxOneSections = boxOneSections;
	}

	@XmlElement(name="notes")
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	@XmlElement(name="issuedBy")
	public CertificateSection getCertificateSection() {
		return certificateSection;
	}

	public void setCertificateSection(CertificateSection certificateSection) {
		this.certificateSection = certificateSection;
	}

	@XmlElement(name="propertySection")
	public PropertySection getPropertySection() {
		return propertySection;
	}

	public void setPropertySection(PropertySection propertySection) {
		this.propertySection = propertySection;
	}

	@XmlElement(name="witnessVictim", type=WitnessVictimPerson.class)
	public List<Person> getWitnessVictims() {
		return witnessVictims;
	}

	public void setWitnessVictims(List<Person> witnessVictims) {
		this.witnessVictims = witnessVictims;
	}

	@XmlElement(name="refersToOperation")
	public String getRefersToOperation() {
		return refersToOperation;
	}

	public void setRefersToOperation(String refersToOperation) {
		this.refersToOperation = refersToOperation;
	}

	@XmlElement(name="defendant", type=DefendantPerson.class)
	public Person getDefendant() {
		return defendant;
	}

	public void setDefendant(Person defendant) {
		this.defendant = defendant;
	}

	@XmlElement(name="offenceSection")
	public OffenceSection getOffenceSection() {
		return offenceSection;
	}

	public void setOffenceSection(OffenceSection offenceSection) {
		this.offenceSection = offenceSection;
	}

	@XmlElement(name="prosecutorSection")
	public ProsecutorSection getProsecutorSection() {
		return prosecutorSection;
	}

	public void setProsecutorSection(ProsecutorSection prosecutorSection) {
		this.prosecutorSection = prosecutorSection;
	}

	@XmlElement(name="courtSection")
	public CourtSection getCourtSection() {
		return courtSection;
	}

	public void setCourtSection(CourtSection courtSection) {
		this.courtSection = courtSection;
	}

	@XmlElement(name="victim", type=VictimPerson.class)
	public Person getVictim() {
		return victim;
	}

	public void setVictim(Person victim) {
		this.victim = victim;
	}

	@XmlElement(name="officer", type=OfficerPerson.class)
	public Person getOfficer() {
		return officer;
	}

	public void setOfficer(Person officer) {
		this.officer = officer;
	}

	@XmlElement(name="policeWitness", type=PoliceWitnessPerson.class)
	public List<Person> getPoliceWinesses() {
		return policeWinesses;
	}

	public void setPoliceWinesses(List<Person> policeWinesses) {
		this.policeWinesses = policeWinesses;
	}

	@XmlElement(name="trialDates")
	public String getTrialDates() {
		return trialDates;
	}

	public void setTrialDates(String trialDates) {
		this.trialDates = trialDates;
	}

	@XmlElement(name="trialReason")
	public String getTrialReason() {
		return trialReason;
	}

	public void setTrialReason(String trialReason) {
		this.trialReason = trialReason;
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
		
}
