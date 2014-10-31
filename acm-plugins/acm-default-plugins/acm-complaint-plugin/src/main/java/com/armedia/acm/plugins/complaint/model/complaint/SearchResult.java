/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

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
public class SearchResult {

	private List<String> result;
	@XmlElements({
		@XmlElement(name="id"),
		@XmlElement(name="existingInitiatorResult"),
		@XmlElement(name="existingPeopleResult")
		
	})
	private Long id;
	private Long page;
	private Long size;
	private String information;
	
	/**
	 * @return the result
	 */
	public List<String> getResult() {
		return result;
	}
	
	/**
	 * @param result the result to set
	 */
	public void setResult(List<String> result) {
		this.result = result;
	}
	
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
	 * @return the page
	 */
	public Long getPage() {
		return page;
	}
	
	/**
	 * @param page the page to set
	 */
	public void setPage(Long page) {
		this.page = page;
	}
	
	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}
	
	/**
	 * @param size the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}
	
	/**
	 * @return the information
	 */
	public String getInformation() {
		return information;
	}
	
	/**
	 * @param information the information to set
	 */
	public void setInformation(String information) {
		this.information = information;
	}
	
}
