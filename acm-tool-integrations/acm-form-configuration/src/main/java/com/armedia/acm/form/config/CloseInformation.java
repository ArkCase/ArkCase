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
public class CloseInformation {

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
	private Date closeDate;
	private String disposition;
	private List<String> dispositions;
	
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

	/**
	 * @return the closeDate
	 */
	public Date getCloseDate() {
		return closeDate;
	}
	
	/**
	 * @param closeDate the closeDate to set
	 */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}
	
	/**
	 * @return the disposition
	 */
	public String getDisposition() {
		return disposition;
	}
	
	/**
	 * @param disposition the disposition to set
	 */
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	
	/**
	 * @return the dispositions
	 */
	public List<String> getDispositions() {
		return dispositions;
	}
	
	/**
	 * @param dispositions the dispositions to set
	 */
	public void setDispositions(List<String> dispositions) {
		this.dispositions = dispositions;
	}
	
}
