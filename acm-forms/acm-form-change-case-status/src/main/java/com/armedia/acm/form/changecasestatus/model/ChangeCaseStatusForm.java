/**
 * 
 */
package com.armedia.acm.form.changecasestatus.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChangeCaseStatusForm {

	private ResolveInformation information;
	@XmlElements({
		@XmlElement(name="approvers"),
		@XmlElement(name="approverItem")
		
	})
	private List<Item> approvers;
	private List<String> approverOptions;
	
	/**
	 * @return the information
	 */
	public ResolveInformation getInformation() {
		return information;
	}
	
	/**
	 * @param information the information to set
	 */
	public void setInformation(ResolveInformation information) {
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
	
}
