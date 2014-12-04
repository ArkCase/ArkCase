/**
 * 
 */
package com.armedia.acm.service.history.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.data.AcmEntity;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_history")
public class AcmHistory implements Serializable, AcmEntity{

	private static final long serialVersionUID = 3919545816704448776L;
	
	@Id
    @Column(name = "cm_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "cm_history_person_id")
	private Long personId;
	
	@Column(name = "cm_history_person_type")
	private String personType;
	
	@Column(name = "cm_history_object_id")
	private Long objectId;
	
	@Column(name = "cm_history_object_type")
	private String objectType;
	
	@Column(name = "cm_history_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name = "cm_history_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column(name = "cm_history_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_history_creator")
	private String creator;
	
	@Column(name = "cm_history_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@Column(name = "cm_history_modifier")
	private String modifier;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPersonId() {
		return personId;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
