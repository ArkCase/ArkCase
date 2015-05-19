/**
 * 
 */
package com.armedia.acm.form.project.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class ProjectMilestone {

	private String number;
	private String text;
	private Date startDate;
	private Date endDate;
	
	
	@XmlElement(name="sectionFourNumber")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@XmlElement(name="sectionFourText")
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@XmlElement(name="sectionFourStartDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@XmlElement(name="sectionFourFinishDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
	
}
