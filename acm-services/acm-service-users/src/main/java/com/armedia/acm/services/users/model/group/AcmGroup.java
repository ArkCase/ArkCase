/**
 * 
 */
package com.armedia.acm.services.users.model.group;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_group")
public class AcmGroup implements Serializable, AcmEntity, AcmLdapEntity{

	private static final long serialVersionUID = -2729731595684630823L;
	
	@Id
	@Column(name = "cm_group_name")
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "cm_group_parent_name")
	@JsonIgnore
	private AcmGroup parentGroup;
	
	@Column(name = "cm_group_description")
	private String description;
	
	@Column(name = "cm_group_type")
	private String type;
	
	@Column(name = "cm_group_status")
	private String status;
	
	@Column(name = "cm_group_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_group_creator")
	private String creator;
	
	@Column(name = "cm_group_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@Column(name = "cm_group_modifier")
	private String modifier;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parentGroup")
	@JsonIgnore
	private List<AcmGroup> childGroups;
	
	@ManyToOne
	@JoinColumn(name = "cm_group_supervisor_id")
	private AcmUser supervisor;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
				name = "acm_group_member",
				joinColumns = { @JoinColumn(name = "cm_group_name", referencedColumnName = "cm_group_name") },
				inverseJoinColumns = { @JoinColumn(name = "cm_user_id", referencedColumnName = "cm_user_id") })
	private Set<AcmUser> members;
	
	@Transient
    private String distinguishedName;
	
	@Transient
	private boolean group;
	
	@PrePersist
    protected void beforeInsert() {
		if (getCreated() == null)
		{
			setCreated(new Date());
		}
		
		if (getModified() == null)
		{
			setModified(new Date());
		}
		
		if (getCreator() == null)
		{
			setCreator("ACM3");
		}
		
		if (getModifier()== null)
		{
			setModifier("ACM3");
		}
		
		if (StringUtils.isEmpty(getType())) 
		{
			setType(AcmGroupType.ADHOC_GROUP);
		}
		
		if (StringUtils.isEmpty(getStatus())) 
		{
			setStatus(AcmGroupStatus.ACTIVE);
		}
    }

    @PreUpdate
    public void beforeUpdate()
    {
    	if (getCreated() == null)
		{
			setCreated(new Date());
		}
		
		if (getModified() == null)
		{
			setModified(new Date());
		}
		
		if (getCreator() == null)
		{
			setCreator("ACM3");
		}
		
		if (getModifier()== null)
		{
			setModifier("ACM3");
		}
		
		if (StringUtils.isEmpty(getType())) 
		{
			setType(AcmGroupType.ADHOC_GROUP);
		}
		
		if (StringUtils.isEmpty(getStatus())) 
		{
			setStatus(AcmGroupStatus.ACTIVE);
		}
    }
	
	public AcmGroup getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(AcmGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public List<AcmGroup> getChildGroups() {
		return childGroups;
	}

	public void setChildGroups(List<AcmGroup> childGroups) {
		this.childGroups = childGroups;
	}

	public AcmUser getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(AcmUser supervisor) {
		this.supervisor = supervisor;
	}

	public Set<AcmUser> getMembers() {
		return members;
	}

	public void setMembers(Set<AcmUser> members) {
		this.members = members;
	}

	@Override
    public String getDistinguishedName()
    {
        return distinguishedName;
    }

    @Override
    public void setDistinguishedName(String distinguishedName)
    {
        this.distinguishedName = distinguishedName;
    }

    @Override
    public boolean isGroup()
    {
        return true;
    }

	public void setGroup(boolean group) {
		this.group = group;
	}
}
