/**
 * 
 */
package com.armedia.acm.form.cost.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author riste.tutureski
 *
 */
public class CostForm {

	private Long id;
	private String user;
	private List<String> userOptions;
	private Long objectId;
	private String objectType;
	private List<String> objectTypeOptions;
	private String objectNumber;
	private List<CostItem> items;
	private String status;
	private List<String> statusOptions;
	
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
}
