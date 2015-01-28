/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author riste.tutureski
 *
 */
public class SearchResult {

	private List<String> result;
	private Long id;
	private Long page;
	private Long size;
	private String information;
	
	/**
	 * @return the result
	 */
	@XmlTransient
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
	@XmlTransient
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
	@XmlTransient
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
	@XmlTransient
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
	@XmlTransient
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
