/**
 * 
 */
package com.armedia.acm.form.cost.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.Details;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.frevvo.model.Options;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.COSTSHEET, namespace=FrevvoFormNamespace.COSTSHEET_NAMESPACE)
public class CostForm extends FrevvoForm {

	private Long id;
	private String user;
	private List<String> userOptions;
	private Long objectId;
	private Map<String, Options> codeOptions;
	private Map<String, Map<String, Details>> codeDetails;
	private String objectType;
	private List<String> objectTypeOptions;
	private String objectNumber;
	private List<CostItem> items;
	private String status;
	private List<String> statusOptions;
	private String details;
	private List<ApproverItem> approvers;
	private List<String> balanceTable;
	
	@XmlElement(name="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(name="user")
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	@XmlTransient
	public List<String> getUserOptions() {
		return userOptions;
	}
	
	public void setUserOptions(List<String> userOptions) {
		this.userOptions = userOptions;
	}
	
	@XmlElement(name="objectId")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@XmlTransient
	public Map<String, Options> getCodeOptions() {
		return codeOptions;
	}

	public void setCodeOptions(Map<String, Options> codeOptions) {
		this.codeOptions = codeOptions;
	}

	@XmlTransient
	public Map<String, Map<String, Details>> getCodeDetails() {
		return codeDetails;
	}

	public void setCodeDetails(Map<String, Map<String, Details>> codeDetails) {
		this.codeDetails = codeDetails;
	}

	@XmlElement(name="type")
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	@XmlTransient
	public List<String> getObjectTypeOptions() {
		return objectTypeOptions;
	}

	public void setObjectTypeOptions(List<String> objectTypeOptions) {
		this.objectTypeOptions = objectTypeOptions;
	}

	@XmlElement(name="objectNumber")
	public String getObjectNumber() {
		return objectNumber;
	}

	public void setObjectNumber(String objectNumber) {
		this.objectNumber = objectNumber;
	}

	@XmlElement(name="costTableItem")
	public List<CostItem> getItems() {
		return items;
	}
	
	public void setItems(List<CostItem> items) {
		this.items = items;
	}

	@XmlElement(name="status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlTransient
	public List<String> getStatusOptions() {
		return statusOptions;
	}

	public void setStatusOptions(List<String> statusOptions) {
		this.statusOptions = statusOptions;
	}

	@XmlElement(name="details")
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@XmlElement(name="approverItem")
	public List<ApproverItem> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<ApproverItem> approvers) {
		this.approvers = approvers;
	}

	@XmlElement(name="balanceTableItem")
	public List<String> getBalanceTable() {
		return balanceTable;
	}

	public void setBalanceTable(List<String> balanceTable) {
		this.balanceTable = balanceTable;
	}
}
