/**
 * 
 */
package com.armedia.acm.form.ebrief.model;


import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.config.xml.OfficerUser;
import com.armedia.acm.form.config.xml.ProsecutorUser;
import com.armedia.acm.form.ebrief.model.xml.EbriefDetails;
import com.armedia.acm.form.ebrief.model.xml.EbriefInformation;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.xml.DefendantPerson;
import com.armedia.acm.services.users.model.AcmUser;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.EBRIEF, namespace=FrevvoFormNamespace.EBRIEF_NAMESPACE)
public class EbriefForm extends FrevvoForm{

	private Long id;
	private EbriefInformation information;
	private List<Person> defendants;
	private AcmUser prosecutor;
	private AcmUser officer;
	private EbriefDetails details;
	private String cmisFolderId;
	
	@XmlElement(name="eBriefId")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlElement(name="eBriefInformation")
	public EbriefInformation getInformation() {
		return information;
	}

	public void setInformation(EbriefInformation information) {
		this.information = information;
	}
	
	@XmlElement(name="defendant", type=DefendantPerson.class)
	public List<Person> getDefendants() {
		return defendants;
	}

	public void setDefendants(List<Person> defendants) {
		this.defendants = defendants;
	}
	
	@XmlElement(name="prosecutor", type=ProsecutorUser.class)
	public AcmUser getProsecutor() {
		return prosecutor;
	}

	public void setProsecutor(AcmUser prosecutor) {
		this.prosecutor = prosecutor;
	}
	
	@XmlElement(name="officer", type=OfficerUser.class)
	public AcmUser getOfficer() {
		return officer;
	}

	public void setOfficer(AcmUser officer) {
		this.officer = officer;
	}

	@XmlElement(name="eBriefDetails")
	public EbriefDetails getDetails() {
		return details;
	}

	public void setDetails(EbriefDetails details) {
		this.details = details;
	}

	@XmlTransient
	public String getCmisFolderId() {
		return cmisFolderId;
	}

	public void setCmisFolderId(String cmisFolderId) {
		this.cmisFolderId = cmisFolderId;
	}
		
}
