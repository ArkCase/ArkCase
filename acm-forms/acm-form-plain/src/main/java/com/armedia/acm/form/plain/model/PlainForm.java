/**
 * 
 */
package com.armedia.acm.form.plain.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class PlainForm {

	private Long id;
	private Long objectId;
	private String objectNumber;
	private String objectType;
	private String cmisFolderId;
	
	@XmlElement(name="id")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlElement(name="objectId")
	public Long getObjectId() {
		return objectId;
	}
	
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	
	@XmlElement(name="objectNumber")
	public String getObjectNumber() {
		return objectNumber;
	}
	
	public void setObjectNumber(String objectNumber) {
		this.objectNumber = objectNumber;
	}
	
	@XmlElement(name="objectType")
	public String getObjectType() {
		return objectType;
	}
	
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	@XmlElement(name="cmisFolderId")
	public String getCmisFolderId() {
		return cmisFolderId;
	}
	
	public void setCmisFolderId(String cmisFolderId) {
		this.cmisFolderId = cmisFolderId;
	}	
}
