package com.armedia.acm.services.users.model.group;

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author riste.tutureski
 */
@Entity
@Table(name = "acm_group")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmGroup implements Serializable, AcmEntity
{
    private static final long serialVersionUID = -2729731595684630823L;

    @Id
    @Column(name = "cm_group_name")
    private String name;

    @Column(name = "cm_group_description")
    private String description;

    @Column(name = "cm_group_type")
    @Enumerated(EnumType.STRING)
    private AcmGroupType type;

    @Column(name = "cm_group_status")
    @Enumerated(EnumType.STRING)
    private AcmGroupStatus status;

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

    @Column(name = "cm_distinguished_name")
    private String distinguishedName;

    @Column(name = "cm_directory_name")
    private String directoryName;

    @ManyToOne
    @JoinColumn(name = "cm_group_supervisor_id")
    private AcmUser supervisor;

    @JsonProperty("members")
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "acm_user_membership",
            joinColumns = { @JoinColumn(name = "cm_group_name", referencedColumnName = "cm_group_name") },
            inverseJoinColumns = { @JoinColumn(name = "cm_user_id", referencedColumnName = "cm_user_id") })
    private Set<AcmUser> userMembers = new HashSet<>();

    @JoinTable(name = "acm_group_membership",
            joinColumns = {
                    @JoinColumn(name = "cm_group_name", referencedColumnName = "cm_group_name", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "cm_member_group_name", referencedColumnName = "cm_group_name", nullable = false)
            })
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<AcmGroup> memberGroups = new HashSet<>();

    @ManyToMany(mappedBy = "memberGroups")
    private Set<AcmGroup> memberOfGroups = new HashSet<>();

    @Column(name = "cm_ascendants")
    private String ascendantsList;

    @PrePersist
    protected void beforeInsert()
    {
        if (type == null)
        {
            type = AcmGroupType.ADHOC_GROUP;
        }

        if (status == null)
        {
            status = AcmGroupStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void beforeUpdate()
    {
        if (type == null)
        {
            type = AcmGroupType.ADHOC_GROUP;
        }

        if (status == null)
        {
            status = AcmGroupStatus.ACTIVE;
        }
    }

    @JsonIgnore
    public Stream<String> getUserMemberDns()
    {
        return userMembers.stream()
                .map(AcmUser::getDistinguishedName);
    }

    @JsonIgnore
    public Stream<String> getUserMemberIds()
    {
        return userMembers.stream()
                .map(AcmUser::getUserId);
    }

    @JsonIgnore
    public Stream<String> getGroupMemberNames()
    {
        return memberGroups.stream()
                .map(AcmGroup::getName);
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for adding
     * userMembers to the group. Don't use getUserMembers().add(..) or getUserMembers().addAll(..)
     *
     * @param user
     */
    public void addUserMember(AcmUser user)
    {
        userMembers.add(user);
        user.addGroup(this);
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for removing
     * userMembers from the group.
     *
     * @param user
     */
    public void removeUserMember(AcmUser user)
    {
        user.getGroups().remove(this);
        userMembers.remove(user);
    }

    public void addGroupMember(AcmGroup group)
    {
        memberGroups.add(group);
        group.getMemberOfGroups().add(this);
    }

    public void removeGroupMember(AcmGroup group)
    {
        memberGroups.remove(group);
        group.getMemberOfGroups().remove(group);
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

    public AcmGroupType getType()
    {
        return type;
    }

    public void setType(AcmGroupType type)
    {
        this.type = type;
    }

    @JsonIgnore
    public boolean isLdapGroup()
    {
        return type == AcmGroupType.LDAP_GROUP;
    }

    public AcmGroupStatus getStatus()
    {
        return status;
    }

    public void setStatus(AcmGroupStatus status)
    {
        this.status = status;
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
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
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

    @JsonIgnore
    public String getDistinguishedName()
    {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName)
    {
        this.distinguishedName = distinguishedName;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public AcmUser getSupervisor()
    {
        return supervisor;
    }

    public void setSupervisor(AcmUser supervisor)
    {
        this.supervisor = supervisor;
    }

    public Set<AcmUser> getUserMembers()
    {
        return userMembers;
    }

    public void setUserMembers(Set<AcmUser> userMembers)
    {
        this.userMembers = userMembers;
    }

    @JsonIgnore
    public boolean hasUserMember(AcmUser user)
    {
        return userMembers != null && userMembers.contains(user);
    }

    public Set<AcmGroup> getMemberGroups()
    {
        return memberGroups;
    }

    public void setMemberGroups(Set<AcmGroup> memberGroups)
    {
        this.memberGroups = memberGroups;
    }

    public Set<AcmGroup> getMemberOfGroups()
    {
        return memberOfGroups;
    }

    public void setMemberOfGroups(Set<AcmGroup> memberOfGroups)
    {
        this.memberOfGroups = memberOfGroups;
    }

    public String getAscendantsList()
    {
        return ascendantsList;
    }

    /**
     * We will use this as pre-computed list of all ascendants found by traversing
     * the full graph of groups and their member groups trying to find path to this group.
     * // TODO: find better separator then `,`, maybe `;` or `:`
     *
     * @return `,` separated list of all ascendants of group
     */
    @JsonIgnore
    public Stream<String> getAscendants()
    {
        if (ascendantsList == null) return Stream.empty();
        return Arrays.stream(ascendantsList.split(","));
    }

    public void addAscendant(String ascendantGroup)
    {
        if (StringUtils.isNotEmpty(ascendantsList))
        {
            ascendantsList += "," + ascendantGroup;
        } else
        {
            ascendantsList = ascendantGroup;
        }
    }

    public void setAscendantsList(String ascendantsList)
    {
        this.ascendantsList = ascendantsList;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcmGroup acmGroup = (AcmGroup) o;
        return Objects.equals(name, acmGroup.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}
