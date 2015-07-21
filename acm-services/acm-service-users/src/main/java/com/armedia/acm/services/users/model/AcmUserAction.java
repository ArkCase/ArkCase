/**
 * 
 */
package com.armedia.acm.services.users.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.data.AcmEntity;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_user_action")
public class AcmUserAction implements Serializable, AcmEntity{

	private static final long serialVersionUID = -8595025198378154826L;

	@Id
    @TableGenerator(name = "acm_user_action_gen",
            table = "acm_user_action_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_user_action",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_user_action_gen")
    @Column(name = "cm_user_action_id")
	private Long id;
	
	@Column(name = "cm_user_action_user_id")
	private String userId;
	
	@Column(name = "cm_user_action_name")
	private String name;
	
	@Column(name = "cm_user_action_object_id")
	private Long objectId;
	
	@Column(name = "cm_user_action_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_user_action_creator")
	private String creator;
	
	@Column(name = "cm_user_action_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@Column(name = "cm_user_action_modifier")
	private String modifier;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getObjectId() {
		return objectId;
	}
	
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public String getModifier() {
		return modifier;
	}
	
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	
}
