/**
 *
 */
package com.armedia.acm.service.objecthistory.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_assignment")
public class AcmAssignment implements Serializable, AcmObject, AcmEntity{

	private static final long serialVersionUID = 6553619780571596758L;

	@Id
    @Column(name = "cm_assignment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cm_assignment_object_id", insertable = true, updatable = false)
	private Long objectId;

	@Column(name = "cm_assignment_object_type", insertable = true, updatable = false)
	private String objectType;

	@Column(name = "cm_assignment_object_title")
	private String objectTitle;

	@Column(name = "cm_assignment_object_name")
	private String objectName;

	@Column(name = "cm_assignment_new_assignee")
	private String newAssignee;

	@Column(name = "cm_assignment_old_assignee")
	private String oldAssignee;

	@Column(name = "cm_assignment_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "cm_assignment_creator")
	private String creator;

	@Column(name = "cm_assignment_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	@Column(name = "cm_assignment_modifier")
	private String modifier;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String getModifier() {
		return modifier;
	}

	@Override
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public Date getModified() {
		return modified;
	}

	@Override
	public void setModified(Date modified) {
		this.modified = modified;
	}
}
