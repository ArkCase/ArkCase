/**
 * 
 */
package com.armedia.acm.form.config;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ResolveInformation {

	@XmlElements({
		@XmlElement(name="id"),
		@XmlElement(name="complaintId"),
		@XmlElement(name="caseId")
		
	})
	private Long id;
	
	@XmlElements({
		@XmlElement(name="number"),
		@XmlElement(name="complaintNumber"),
		@XmlElement(name="caseNumber")
		
	})
	private String number;
	
	@XmlElements({
		@XmlElement(name="date"),
		@XmlElement(name="closeDate"),
		@XmlElement(name="changeDate")
		
	})
	private Date date;
	
	@XmlElements({
		@XmlElement(name="option"),
		@XmlElement(name="disposition"),
		@XmlElement(name="status")
		
	})
	private String option;
	private List<String> resolveOptions;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public List<String> getResolveOptions() {
		return resolveOptions;
	}

	public void setResolveOptions(List<String> resolveOptions) {
		this.resolveOptions = resolveOptions;
	}
	
}
