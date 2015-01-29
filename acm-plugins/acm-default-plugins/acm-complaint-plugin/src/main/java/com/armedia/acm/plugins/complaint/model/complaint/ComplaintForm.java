package com.armedia.acm.plugins.complaint.model.complaint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.GeneralPostalAddress;
import com.armedia.acm.plugins.complaint.model.complaint.xml.InitiatorContact;
import com.armedia.acm.plugins.complaint.model.complaint.xml.PeopleContact;


/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.COMPLAINT, namespace=FrevvoFormNamespace.COMPLAINT_NAMESPACE)
public class ComplaintForm {
	
	private Long complaintId;
	private String complaintNumber;
	private String complaintTitle;
	private String category;
	private List<String> categories;
	private String complaintDescription;
	private String priority;
	private List<String> priorities;
	private Date date;
	private String complaintTag;
	private String frequency;
	private List<String> frequencies;
	private PostalAddress location;
	private Contact initiator;
	private List<Contact> people;
    private String cmisFolderId;
    private List<ParticipantItem> participants;
    private Map<String, Strings> participantsOptions = new HashMap<>();
    private List<String> participantsTypeOptions;

	public Long getComplaintId() {
		return complaintId;
	}

	public void setComplaintId(Long complaintId) {
		this.complaintId = complaintId;
	}

	public String getComplaintNumber() {
		return complaintNumber;
	}

	public void setComplaintNumber(String complaintNumber) {
		this.complaintNumber = complaintNumber;
	}

	public String getComplaintTitle() {
		return complaintTitle;
	}

	public void setComplaintTitle(String complaintTitle) {
		this.complaintTitle = complaintTitle;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@XmlTransient
	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getComplaintDescription() {
		return complaintDescription;
	}

	public void setComplaintDescription(String complaintDescription) {
		this.complaintDescription = complaintDescription;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@XmlTransient
	public List<String> getPriorities() {
		return priorities;
	}

	public void setPriorities(List<String> priorities) {
		this.priorities = priorities;
	}

	@XmlElement(name="incidentDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComplaintTag() {
		return complaintTag;
	}

	public void setComplaintTag(String complaintTag) {
		this.complaintTag = complaintTag;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	@XmlTransient
	public List<String> getFrequencies() {
		return frequencies;
	}

	public void setFrequencies(List<String> frequencies) {
		this.frequencies = frequencies;
	}

	@XmlElement(name="location", type=GeneralPostalAddress.class)
	public PostalAddress getLocation() {
		return location;
	}

	public void setLocation(PostalAddress location) {
		this.location = location;
	}

	@XmlElement(name="initiator", type=InitiatorContact.class)
	public Contact getInitiator() {
		return initiator;
	}

	public void setInitiator(Contact initiator) {
		this.initiator = initiator;
	}

	@XmlElement(name="people", type=PeopleContact.class)
	public List<Contact> getPeople() {
		return people;
	}

	public void setPeople(List<Contact> people) {
		this.people = people;
	}

	@XmlTransient
    public String getCmisFolderId()
    {
        return cmisFolderId;
    }

    public void setCmisFolderId(String cmisFolderId)
    {
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
	public Map<String, Strings> getParticipantsOptions() {
		return participantsOptions;
	}

	public void setParticipantsOptions(Map<String, Strings> participantsOptions) {
		this.participantsOptions = participantsOptions;
	}

	@XmlTransient
	public List<String> getParticipantsTypeOptions() {
		return participantsTypeOptions;
	}

	public void setParticipantsTypeOptions(List<String> participantsTypeOptions) {
		this.participantsTypeOptions = participantsTypeOptions;
	}

}
