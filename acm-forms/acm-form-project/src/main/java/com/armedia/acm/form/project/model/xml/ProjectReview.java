/**
 * 
 */
package com.armedia.acm.form.project.model.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class ProjectReview {

	private String text;
	private String yesNo;
	private String inits;
	
	@XmlElement(name="sectionFiveTable1Title")
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@XmlElement(name="sectionFiveTable1YesNo")
	public String getYesNo() {
		return yesNo;
	}
	
	public void setYesNo(String yesNo) {
		this.yesNo = yesNo;
	}
	
	@XmlElement(name="sectionFiveTable1Inits")
	public String getInits() {
		return inits;
	}
	
	public void setInits(String inits) {
		this.inits = inits;
	}
	
}
