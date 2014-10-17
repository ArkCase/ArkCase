/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import java.util.List;

import com.armedia.acm.plugins.addressable.model.ContactMethod;

/**
 * @author riste.tutureski
 *
 */
public class Approver {

	private String approverId;
	private List<String> approvers;
	
	/**
	 * @return the approverId
	 */
	public String getApproverId() {
		return approverId;
	}
	
	/**
	 * @param approverId the approverId to set
	 */
	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}
	
	/**
	 * @return the approvers
	 */
	public List<String> getApprovers() {
		return approvers;
	}
	
	/**
	 * @param approvers the approvers to set
	 */
	public void setApprovers(List<String> approvers) {
		this.approvers = approvers;
	}
	
}
