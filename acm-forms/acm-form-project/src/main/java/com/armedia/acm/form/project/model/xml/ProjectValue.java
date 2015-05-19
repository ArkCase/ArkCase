/**
 * 
 */
package com.armedia.acm.form.project.model.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class ProjectValue {

	private String description;
	private String section;
	private String value;
	
	@XmlElement(name="sectionFiveTable2Description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name="sectionFiveTable2Section")
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	@XmlElement(name="sectionFiveTable2Value")
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
