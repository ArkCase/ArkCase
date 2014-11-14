/**
 * 
 */
package com.armedia.acm.form.closecase.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.armedia.acm.form.config.CloseInformation;
import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CloseCaseForm {

	private CloseInformation information;
	@XmlElements({
		@XmlElement(name="approvers"),
		@XmlElement(name="approverItem")
		
	})
	private List<Item> approvers;
	private List<String> approverOptions;
	private String description;
	
	/**
	 * @return the information
	 */
	public CloseInformation getInformation() {
		return information;
	}
	
	/**
	 * @param information the information to set
	 */
	public void setInformation(CloseInformation information) {
		this.information = information;
	}
	
	/**
	 * @return the approvers
	 */
	public List<Item> getApprovers() {
		return approvers;
	}
	
	/**
	 * @param approvers the approvers to set
	 */
	public void setApprovers(List<Item> approvers) {
		this.approvers = approvers;
	}
	
	/**
	 * @return the approverOptions
	 */
	public List<String> getApproverOptions() {
		return approverOptions;
	}
	
	/**
	 * @param approverOptions the approverOptions to set
	 */
	public void setApproverOptions(List<String> approverOptions) {
		this.approverOptions = approverOptions;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
