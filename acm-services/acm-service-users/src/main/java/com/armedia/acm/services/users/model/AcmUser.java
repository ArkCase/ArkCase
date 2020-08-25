package com.armedia.acm.services.users.model;

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

import com.armedia.acm.data.converter.LocalDateConverter;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "acm_user")
public class AcmUser implements Serializable
{
    private static final long serialVersionUID = 3399640646540732944L;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_identifier", referencedColumnName = "cm_id", nullable = false)
    private AcmUserIdentifier identifier = new AcmUserIdentifier();

    @Id
    @Column(name = "cm_user_id")
    private String userId;

    @Column(name = "cm_full_name")
    private String fullName;

    @Column(name = "cm_first_name")
    private String firstName;

    @Column(name = "cm_last_name")
    private String lastName;

    @Column(name = "cm_user_directory_name", updatable = false)
    private String userDirectoryName;

    @Column(name = "cm_samaccountname")
    private String sAMAccountName;

    @Column(name = "cm_user_principal_name")
    private String userPrincipalName;

    @Column(name = "cm_user_created", insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_user_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_user_deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @Column(name = "cm_user_state")
    @Enumerated(EnumType.STRING)
    private AcmUserState userState;

    @Column(name = "cm_mail")
    private String mail;

    @Column(name = "cm_distinguished_name")
    private String distinguishedName;

    @Column(name = "cm_uid")
    private String uid;

    @Column(name = "cm_country")
    private String country;

    @Column(name = "cm_country_abbreviation")
    private String countryAbbreviation;

    @Column(name = "cm_department")
    private String department;

    @Column(name = "cm_company")
    private String company;

    @Column(name = "cm_title")
    private String title;

    @Column(name = "cm_lang")
    private String lang;

    @Column(name = "cm_pwd_ex_date")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate passwordExpirationDate;

    @Embedded
    private PasswordResetToken passwordResetToken;

    @ManyToMany(mappedBy = "userMembers", cascade = { CascadeType.MERGE })
    @JsonIgnore
    private Set<AcmGroup> groups = new HashSet<>();

    @JsonIgnore
    public Stream<String> getGroupNames()
    {
        return groups.stream().map(AcmGroup::getName);
    }

    @JsonIgnore
    public Set<AcmGroup> getLdapGroups()
    {
        return groups.stream().filter(AcmGroup::isLdapGroup).collect(Collectors.toSet());
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for adding groups to the user. Don't use
     * getGroups().add(..) or getGroups().addAll(..)
     *
     * @param group
     */
    public void addGroup(AcmGroup group)
    {
        groups.add(group);
        group.getUserMembers(false).add(this);
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for removing groups from the user.
     *
     * @param group
     */
    public void removeGroup(AcmGroup group)
    {
        groups.remove(group);
        group.getUserMembers(false).remove(this);
    }

    @PrePersist
    public void preInsert()
    {
        setCreated(new Date());
        setModified(new Date());
    }

    @PreUpdate
    public void preUpdate()
    {
        setModified(new Date());
    }

    @JsonIgnore
    public Long getIdentifier()
    {
        return identifier.getId();
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserDirectoryName()
    {
        return userDirectoryName;
    }

    public void setUserDirectoryName(String userDirectoryName)
    {
        this.userDirectoryName = userDirectoryName;
    }

    @JsonIgnore
    public String getsAMAccountName()
    {
        return sAMAccountName;
    }

    public void setsAMAccountName(String sAMAccountName)
    {
        this.sAMAccountName = sAMAccountName;
    }

    @JsonIgnore
    public String getUserPrincipalName()
    {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName)
    {
        this.userPrincipalName = userPrincipalName;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public Date getDeletedAt()
    {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt)
    {
        this.deletedAt = deletedAt;
    }

    public AcmUserState getUserState()
    {
        return userState;
    }

    public void setUserState(AcmUserState userState)
    {
        this.userState = userState;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public Set<AcmGroup> getGroups()
    {
        return groups;
    }

    public void setGroups(Set<AcmGroup> groups)
    {
        this.groups = groups;
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

    @JsonIgnore
    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCountryAbbreviation()
    {
        return countryAbbreviation;
    }

    public void setCountryAbbreviation(String countryAbbreviation)
    {
        this.countryAbbreviation = countryAbbreviation;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLang()
    {
        return lang;
    }

    public void setLang(String lang)
    {
        this.lang = lang;
    }

    public LocalDate getPasswordExpirationDate()
    {
        return passwordExpirationDate;
    }

    public void setPasswordExpirationDate(LocalDate passwordExpirationDate)
    {
        this.passwordExpirationDate = passwordExpirationDate;
    }

    public PasswordResetToken getPasswordResetToken()
    {
        return passwordResetToken;
    }

    public void setPasswordResetToken(PasswordResetToken passwordResetToken)
    {
        this.passwordResetToken = passwordResetToken;
    }

    public void invalidateUser(AcmUser acmUser)
    {
        acmUser.setUserState(AcmUserState.INVALID);
        String invalidDn = MapperUtils.appendToDn(acmUser.getDistinguishedName(), AcmLdapConstants.DC_DELETED);
        acmUser.setDistinguishedName(invalidDn);
        acmUser.setDeletedAt(new Date());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AcmUser acmUser = (AcmUser) o;
        return Objects.equals(userId, acmUser.userId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .toString();
    }
}
