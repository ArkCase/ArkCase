/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import com.armedia.acm.plugins.person.model.Person;


/**
 * @author riste.tutureski
 *
 */
public class CloseComplaintForm {

	private CloseComplaintInformation information;
	private ReferExternal referExternal;
	private ExistingCase existingCase;
	private Approver approver;
	private String description;
	
	
	/**
	 * @return the information
	 */
	public CloseComplaintInformation getInformation() {
		return information;
	}

	/**
	 * @param information the information to set
	 */
	public void setInformation(CloseComplaintInformation information) {
		this.information = information;
	}

	/**
	 * @return the referExternal
	 */
	public ReferExternal getReferExternal() {
		return referExternal;
	}

	/**
	 * @param referExternal the referExternal to set
	 */
	public void setReferExternal(ReferExternal referExternal) {
		this.referExternal = referExternal;
	}

	/**
	 * @return the existingCase
	 */
	public ExistingCase getExistingCase() {
		return existingCase;
	}

	/**
	 * @param existingCase the existingCase to set
	 */
	public void setExistingCase(ExistingCase existingCase) {
		this.existingCase = existingCase;
	}

	/**
	 * @return the approver
	 */
	public Approver getApprover() {
		return approver;
	}

	/**
	 * @param approver the approver to set
	 */
	public void setApprover(Approver approver) {
		this.approver = approver;
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
