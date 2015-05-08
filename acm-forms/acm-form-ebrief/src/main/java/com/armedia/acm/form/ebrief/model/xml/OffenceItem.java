/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class OffenceItem {

	private String act;
	private String section;
	private String description;
	
	@XmlElement(name="offenceAct")
	public String getAct() {
		return act;
	}
	
	public void setAct(String act) {
		this.act = act;
	}
	
	@XmlElement(name="offenceSection")
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	@XmlElement(name="offenceDescription")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
