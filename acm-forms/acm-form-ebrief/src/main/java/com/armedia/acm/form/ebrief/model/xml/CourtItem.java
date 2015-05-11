/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class CourtItem {

	private String number;
	private String section;
	private String fine;
	private String imp;
	private String other;
	
	@XmlElement(name="courtTableNo")
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	@XmlElement(name="courtTableSection")
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	@XmlElement(name="courtTableFine")
	public String getFine() {
		return fine;
	}
	
	public void setFine(String fine) {
		this.fine = fine;
	}
	
	@XmlElement(name="courtTableImp")
	public String getImp() {
		return imp;
	}
	
	public void setImp(String imp) {
		this.imp = imp;
	}
	
	@XmlElement(name="courtTableOther")
	public String getOther() {
		return other;
	}
	
	public void setOther(String other) {
		this.other = other;
	}	
}
