/**
 * 
 */
package com.armedia.acm.form.cost.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.COST, namespace=FrevvoFormNamespace.COST_NAMESPACE)
public class CostForm {

	private Long id;
	private String user;
	private List<String> userOptions;
	private Long objectId;
	private Map<String, List<String>> codeOptions;
	private String objectType;
	private List<String> objectTypeOptions;
	private String objectNumber;
	private List<CostItem> items;
	private String status;
	private List<String> statusOptions;
	private String initData;
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
	public Map<String, List<String>> getCodeOptions() {
		return codeOptions;
	}

	public void setCodeOptions(Map<String, List<String>> codeOptions) {
		this.codeOptions = codeOptions;
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

	@XmlElement(name="initData")
	public String getInitData() {
		return initData;
	}

	public void setInitData(String initData) {
		this.initData = initData;
	}

	@XmlElement(name="balanceTableItem")
	public List<String> getBalanceTable() {
		return balanceTable;
	}

	public void setBalanceTable(List<String> balanceTable) {
		this.balanceTable = balanceTable;
	}
}
