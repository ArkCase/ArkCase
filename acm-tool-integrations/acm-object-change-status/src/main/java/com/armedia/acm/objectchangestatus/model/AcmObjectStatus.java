/**
 * 
 */
package com.armedia.acm.objectchangestatus.model;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectStatus {

	private Long objectId;
	private String objectType;
	private String status;
	
	public AcmObjectStatus(Long objectId, String objectType, String status)
	{
		this.objectId = objectId;
		this.objectType = objectType;
		this.status = status;
	}
	
	public Long getObjectId() {
		return objectId;
	}
	
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectType() {
		return objectType;
	}
	
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
