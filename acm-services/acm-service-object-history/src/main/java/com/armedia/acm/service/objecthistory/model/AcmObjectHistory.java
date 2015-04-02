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
import javax.persistence.Lob;
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
@Table(name = "acm_object_history")
public class AcmObjectHistory implements Serializable, AcmObject, AcmEntity{

	private static final long serialVersionUID = 9140143221014764628L;

	@Id
    @Column(name = "cm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "cm_object_id", insertable = true, updatable = false)
	private Long objectId;
	
	@Column(name = "cm_object_type", insertable = true, updatable = false)
	private String objectType;
	
	@Lob
	@Column(name = "cm_object_string", insertable = true, updatable = false)
	private String objectString;
	
	@Column(name = "cm_user_id", insertable = true, updatable = false)
	private String userId;
	
	@Column(name = "cm_date", insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@Column(name = "cm_type", insertable = true, updatable = false)
	private String type;
	
	@Column(name = "cm_created", insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_creator", insertable = true, updatable = false)
	private String creator;
	
	@Column(name = "cm_modified", insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@Column(name = "cm_modifier", insertable = true, updatable = false)
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

	public String getObjectString() {
		return objectString;
	}

	public void setObjectString(String objectString) {
		this.objectString = objectString;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	public String getCreator() {
		return creator;
	}
	
	@Override
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	@Override
	public Date getModified() {
		return modified;
	}
	
	@Override
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	@Override
	public String getModifier() {
		return modifier;
	}
	
	@Override
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
}
