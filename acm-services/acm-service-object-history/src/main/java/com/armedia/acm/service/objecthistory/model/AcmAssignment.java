/**
 * 
 */
package com.armedia.acm.service.objecthistory.model;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class AcmAssignment {

	private Long objectId;
	private String objectType;
	private String objectTitle;
	private String objectName;
	private String newAssignee;
	private String oldAssignee;
	private Date date;
	
	public AcmAssignment() {
		setDate(new Date());
	}
	
	public AcmAssignment(Long objectId, String objectType, String objectTitle, String objectName, String newAssignee, String oldAssignee) {
		setObjectId(objectId);
		setObjectType(objectType);
		setObjectTitle(objectTitle);
		setObjectName(objectName);
		setNewAssignee(newAssignee);
		setOldAssignee(oldAssignee);
		setDate(new Date());
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
	
	public String getObjectTitle() {
		return objectTitle;
	}

	public void setObjectTitle(String objectTitle) {
		this.objectTitle = objectTitle;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
