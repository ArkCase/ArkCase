package com.armedia.acm.services.users.model.group;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author riste.tutureski
 */
@Entity
@Table(name = "acm_group")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@LdapGroupNameValidation(message = "Ldap group name validation")
public class AcmGroup implements Serializable, AcmEntity
{
    private static final long serialVersionUID = -2729731595684630823L;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_identifier", referencedColumnName = "cm_id", nullable = false)
    private AcmGroupIdentifier identifier = new AcmGroupIdentifier();

    @Id
    @Column(name = "cm_group_name")
    private String name;

    @Column(name = "cm_group_display_name")
    private String displayName;

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
    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(name = "acm_user_membership", joinColumns = {
            @JoinColumn(name = "cm_group_name", referencedColumnName = "cm_group_name") }, inverseJoinColumns = {
                    @JoinColumn(name = "cm_user_id", referencedColumnName = "cm_user_id") })
    private Set<AcmUser> userMembers = new HashSet<>();

    @JoinTable(name = "acm_group_membership", joinColumns = {
            @JoinColumn(name = "cm_group_name", referencedColumnName = "cm_group_name", nullable = false)
    }, inverseJoinColumns = {
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
        return userMembers.stream().map(AcmUser::getDistinguishedName);
    }

    @JsonIgnore
    public Stream<String> getUserMemberIds()
    {
        return userMembers.stream().map(AcmUser::getUserId);
    }

    @JsonIgnore
    public Stream<String> getGroupMemberNames()
    {
        return memberGroups.stream().map(AcmGroup::getName);
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for adding userMembers to the group.
     * Don't use getUserMembers().add(..) or getUserMembers().addAll(..)
     *
     * @param user
     */
    public void addUserMember(AcmUser user)
    {
        userMembers.add(user);
        user.addGroup(this);
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for removing userMembers from the group.
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
        group.addToGroup(this);
    }

    public void removeGroupMember(AcmGroup group)
    {
        memberGroups.remove(group);
        group.removeFromGroup(this);
    }

    public void removeMembers()
    {
        memberGroups.forEach(memberGroup -> memberGroup.removeFromGroup(this));
        memberGroups.clear();
    }

    public void removeUserMembers()
    {
        userMembers.forEach(user -> user.setModified(new Date()));
        this.setUserMembers(new HashSet<>());
    }

    public void addToGroup(AcmGroup group)
    {
        memberOfGroups.add(group);
    }

    public void removeFromGroup(AcmGroup group)
    {
        memberOfGroups.remove(group);
    }

    public void removeAsMemberOf()
    {
        memberOfGroups.forEach(memberOfGroup -> memberOfGroup.removeGroupMember(this));
        memberOfGroups.clear();
    }

    @JsonIgnore
    public Long getIdentifier()
    {
        return identifier.getId();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
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

    /**
     *
     * @return all users regardless of status
     */
    @Deprecated
    public Set<AcmUser> getUserMembers()
    {
        return userMembers;
    }

    /**
     *
     * @param onlyValidUsers
     * @return all users if onlyValidUsers is false, otherwise users with VALID state
     */
    public Set<AcmUser> getUserMembers(boolean onlyValidUsers)
    {
        if (!onlyValidUsers)
        {
            return userMembers;
        }
        else
        {
            return userMembers.stream()
                    .filter(user -> user.getUserState().equals(AcmUserState.VALID))
                    .collect(Collectors.toSet());
        }
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

    public void setAscendantsList(String ascendantsList)
    {
        this.ascendantsList = ascendantsList;
    }

    /**
     * We will use this as pre-computed list of all ascendants found by traversing the full graph of groups and their
     * member groups trying to find path to this group.
     *
     * @return {@value AcmGroupConstants.ASCENDANTS_STRING_DELIMITER} separated list of all ascendants of group
     */
    @JsonIgnore
    public Stream<String> getAscendantsStream()
    {
        if (StringUtils.isBlank(ascendantsList))
        {
            return Stream.empty();
        }
        return Arrays.stream(ascendantsList.split(Pattern.quote(AcmGroupConstants.ASCENDANTS_STRING_DELIMITER))).sorted();
    }

    public Set<String> getAscendants()
    {
        if (StringUtils.isBlank(ascendantsList))
            return new TreeSet<>();
        return Arrays.stream(ascendantsList.split(Pattern.quote(AcmGroupConstants.ASCENDANTS_STRING_DELIMITER)))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public void addAscendant(String ascendantGroup)
    {
        Set<String> ascendants = getAscendants();
        ascendants.add(ascendantGroup);
        ascendantsList = ascendants.stream()
                .sorted()
                .collect(Collectors.joining(AcmGroupConstants.ASCENDANTS_STRING_DELIMITER));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        AcmGroup acmGroup = (AcmGroup) o;
        if (name != null)
        {
            return name.equals(acmGroup.name);
        }
        return acmGroup.name == null;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name == null ? null : name.toLowerCase());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("name", name).toString();
    }
}
