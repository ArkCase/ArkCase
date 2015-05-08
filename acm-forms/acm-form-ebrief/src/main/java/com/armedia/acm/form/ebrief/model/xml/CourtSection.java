/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class CourtSection {

	private String court;
	private Date date;
	private String jpsjsm;
	private String defenceType;
	private List<CourtItem> items;
	private String briefNumber;
	private String fileNumber;
	
	@XmlElement(name="court")
	public String getCourt() {
		return court;
	}
	
	public void setCourt(String court) {
		this.court = court;
	}
	
	@XmlElement(name="courtDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	@XmlElement(name="courtJPSJSM")
	public String getJpsjsm() {
		return jpsjsm;
	}
	
	public void setJpsjsm(String jpsjsm) {
		this.jpsjsm = jpsjsm;
	}
	
	@XmlElement(name="courtDefenceType")
	public String getDefenceType() {
		return defenceType;
	}
	
	public void setDefenceType(String defenceType) {
		this.defenceType = defenceType;
	}
	
	@XmlElement(name="courtTableItem")
	public List<CourtItem> getItems() {
		return items;
	}
	
	public void setItems(List<CourtItem> items) {
		this.items = items;
	}
	
	@XmlElement(name="courtBriefNumber")
	public String getBriefNumber() {
		return briefNumber;
	}
	
	public void setBriefNumber(String briefNumber) {
		this.briefNumber = briefNumber;
	}
	
	@XmlElement(name="courtFileNumber")
	public String getFileNumber() {
		return fileNumber;
	}
	
	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}	
}
