/**
 * 
 */
package com.armedia.acm.service.objecthistory.model;

/**
 * @author riste.tutureski
 *
 */
public class AssigneeChangeInfo {

	private Long objectId;
	private String objectType;
	private String objectNumber;
	private String objectName;
	private String newAssignee;
	private String oldAssignee;
	
	public AssigneeChangeInfo() {
		
	}
	
	public AssigneeChangeInfo(Long objectId, String objectType, String objectNumber, String objectName, String newAssignee, String oldAssignee) {
		setObjectId(objectId);
		setObjectType(objectType);
		setObjectNumber(objectNumber);
		setObjectName(objectName);
		setNewAssignee(newAssignee);
		setOldAssignee(oldAssignee);
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
	
	public String getObjectNumber() {
		return objectNumber;
	}
	
	public void setObjectNumber(String objectNumber) {
		this.objectNumber = objectNumber;
	}
	
	public String getObjectName() {
		return objectName;
	}
	
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	public String getNewAssignee() {
		return newAssignee;
	}
	
	public void setNewAssignee(String newAssignee) {
		this.newAssignee = newAssignee;
	}
	
	public String getOldAssignee() {
		return oldAssignee;
	}
	
	public void setOldAssignee(String oldAssignee) {
		this.oldAssignee = oldAssignee;
	}
}
