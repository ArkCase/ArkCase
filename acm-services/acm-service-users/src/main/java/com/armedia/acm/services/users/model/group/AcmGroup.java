/**
 *
 */
package com.armedia.acm.services.users.model.group;

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author riste.tutureski
 */
@Entity
@Table(name = "acm_group")
public class AcmGroup implements Serializable, AcmEntity
{

    private static final long serialVersionUID = -2729731595684630823L;

    @Id
    @Column(name = "cm_group_name")
    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
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
            joinColumns = {@JoinColumn(name = "cm_group_name", referencedColumnName = "cm_group_name")},
            inverseJoinColumns = {@JoinColumn(name = "cm_user_id", referencedColumnName = "cm_user_id")})
    private Set<AcmUser> members;

    public AcmGroup getParentGroup()
    {
        return parentGroup;
    }

    public void setParentGroup(AcmGroup parentGroup)
    {
        if (parentGroup != null)
        {
            if (parentGroup.getChildGroups() == null)
            {
                parentGroup.setChildGroups(new ArrayList<AcmGroup>());
            }

            parentGroup.getChildGroups().add(this);

        } else
        {
            if (getParentGroup() != null &&
                    getParentGroup().getChildGroups() != null &&
                    getParentGroup().getChildGroups().contains(this))
            {
                getParentGroup().getChildGroups().remove(this);
            }
        }

        this.parentGroup = parentGroup;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String getCreator()
    {
        return creator;
    }

    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    @Override
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    @Override
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public List<AcmGroup> getChildGroups()
    {
        return childGroups;
    }

    public void setChildGroups(List<AcmGroup> childGroups)
    {
        if (childGroups != null)
        {
            for (AcmGroup child : childGroups)
            {
                child.setParentGroup(this);
            }
        }

        this.childGroups = childGroups;
    }

    public AcmUser getSupervisor()
    {
        return supervisor;
    }

    public void setSupervisor(AcmUser supervisor)
    {
        this.supervisor = supervisor;
    }

    public Set<AcmUser> getMembers()
    {
        return members;
    }

    public void setMembers(Set<AcmUser> members)
    {
        // Bidirectional ManyToMany relation
        if (members != null)
        {
            for (AcmUser member : members)
            {
                if (member.getGroups() != null && !member.getGroups().contains(this))
                {
                    member.getGroups().add(this);
                }
            }
        }

        this.members = members;
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for adding
     * members to the group. Don't use getMembers().add(..) or getMembers().addAll(..)
     *
     * @param member
     */
    public void addMember(AcmUser member)
    {
        if (member != null)
        {
            if (getMembers() == null)
            {
                setMembers(new HashSet<>());
            }

            getMembers().add(member);

            if (member.getGroups() != null && !member.getGroups().contains(this))
            {
                member.addGroup(this);
            }
        }
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for removing
     * members from the group.
     *
     * @param member
     */
    public void removeMember(AcmUser member)
    {
        if (member != null)
        {
            if (getMembers() != null)
            {
                if (member.getGroups().contains(this))
                {
                    member.getGroups().remove(this);
                }

                if (getMembers().contains(member))
                {
                    getMembers().remove(member);
                }

            }
        }
    }

    @Override
    @JsonIgnore
    public int hashCode()
    {
        if (getName() == null)
        {
            return 0;
        } else
        {
            return getName().hashCode();
        }
    }

    @Override
    @JsonIgnore
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AcmGroup))
        {
            return false;
        }

        AcmGroup group = (AcmGroup) obj;

        if (group.getName() == null && getName() == null)
        {
            return true;
        }

        if (group.getName() == null && getName() != null)
        {
            return false;
        }

        return group.getName().equals(getName());
    }
}
