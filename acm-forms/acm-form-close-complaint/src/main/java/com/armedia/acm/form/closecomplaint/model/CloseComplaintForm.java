/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.form.config.Item;
import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.form.config.xml.ComplaintResolveInformation;


/**
 * @author riste.tutureski
 *
 */
public class CloseComplaintForm {

	private ResolveInformation information;
	private ReferExternal referExternal;
	private ExistingCase existingCase;
	private List<Item> approvers;
	private String description;


	@XmlElement(name="information", type=ComplaintResolveInformation.class)
	public ResolveInformation getInformation() {
		return information;
	}

	public void setInformation(ResolveInformation information) {
		this.information = information;
	}

	public ReferExternal getReferExternal() {
		return referExternal;
	}

	public void setReferExternal(ReferExternal referExternal) {
		this.referExternal = referExternal;
	}

	public ExistingCase getExistingCase() {
		return existingCase;
	}

	public void setExistingCase(ExistingCase existingCase) {
		this.existingCase = existingCase;
	}

	@XmlElement(name="approverItem", type=ApproverItem.class)
	public List<Item> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<Item> approvers) {
		this.approvers = approvers;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
