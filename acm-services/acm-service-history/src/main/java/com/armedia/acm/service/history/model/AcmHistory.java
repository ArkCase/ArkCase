/**
 * 
 */
package com.armedia.acm.service.history.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

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
    @TableGenerator(name = "acm_history_gen",
            table = "acm_history_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_history",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_history_gen")
    @Column(name = "cm_history_id")
	private Long id;

	@Column(name = "cm_history_person_id", insertable = true, updatable = false)
	private Long personId;
	
	@Column(name = "cm_history_person_type", insertable = true, updatable = false)
	private String personType;
	
	@Column(name = "cm_history_object_id", insertable = true, updatable = false)
	private Long objectId;
	
	@Column(name = "cm_history_object_type", insertable = true, updatable = false)
	private String objectType;
	
	@Column(name = "cm_history_start_date", insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name = "cm_history_end_date", insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column(name = "cm_history_created", insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_history_creator", insertable = true, updatable = false)
	private String creator;

	@Column(name = "cm_history_modified", insertable = true, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@Column(name = "cm_history_modifier", insertable = true, updatable = false)
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
