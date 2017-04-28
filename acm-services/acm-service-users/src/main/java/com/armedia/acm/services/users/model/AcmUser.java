package com.armedia.acm.services.users.model;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "acm_user")
public class AcmUser implements Serializable, AcmLdapUser
{
    private static final long serialVersionUID = 3399640646540732944L;

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

    @JsonFormat(pattern = AcmUsersConstants.SOLR_DATE_FORMAT)
    @Column(name = "cm_user_created", insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @JsonFormat(pattern = AcmUsersConstants.SOLR_DATE_FORMAT)
    @Column(name = "cm_user_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_user_state")
    private String userState;

    @Column(name = "cm_mail")
    private String mail;

    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<AcmGroup> groups;

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

    @Transient
    private String sortableValue;

    @Transient
    private Set<String> ldapGroups = new HashSet<>();

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

    public String getsAMAccountName()
    {
        return sAMAccountName;
    }

    public void setsAMAccountName(String sAMAccountName)
    {
        this.sAMAccountName = sAMAccountName;
    }

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

    public String getUserState()
    {
        return userState;
    }

    public void setUserState(String userState)
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
        // Bidirectional ManyToMany relation
        if (groups != null)
        {
            for (AcmGroup group : groups)
            {
                if (group.getMembers() != null && !group.getMembers().contains(this))
                {
                    group.getMembers().add(this);
                }
            }
        }

        this.groups = groups;
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for adding groups to the user. Don't use getGroups().add(..)
     * or getGroups().addAll(..)
     *
     * @param group
     */
    public void addGroup(AcmGroup group)
    {
        if (group != null)
        {
            if (getGroups() == null)
            {
                setGroups(new HashSet<>());
            }

            getGroups().add(group);

            if (group.getMembers() != null && !group.getMembers().contains(this))
            {
                group.getMembers().add(this);
            }
        }
    }

    /**
     * Because of bidirectional ManyToMany relation, this method should be used for removing groups from the user.
     *
     * @param group
     */
    public void removeGroup(AcmGroup group)
    {
        if (group != null)
        {
            if (getGroups() != null)
            {
                if (getGroups().contains(group))
                {
                    getGroups().remove(group);
                }

                if (group.getMembers() != null && group.getMembers().contains(this))
                {
                    group.getMembers().remove(this);
                }
            }
        }
    }

    @Override
    @JsonIgnore
    public int hashCode()
    {
        if (getUserId() == null)
        {
            return 0;
        } else
        {
            return getUserId().hashCode();
        }
    }

    @Override
    @JsonIgnore
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AcmUser))
        {
            return false;
        }

        AcmUser user = (AcmUser) obj;

        if (user.getUserId() == null && getUserId() == null)
        {
            return true;
        }

        if (user.getUserId() == null && getUserId() != null)
        {
            return false;
        }

        return user.getUserId().equals(getUserId());
    }

    @Override
    @JsonIgnore
    public Set<String> getLdapGroups()
    {
        return ldapGroups;
    }

    public void setLdapGroups(Set<String> ldapGroups)
    {
        this.ldapGroups = ldapGroups;
    }

    @Override
    @JsonIgnore
    public String getDistinguishedName()
    {
        return distinguishedName;
    }

    @Override
    public void setDistinguishedName(String distinguishedName)
    {
        this.distinguishedName = distinguishedName;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    @JsonIgnore
    public String getSortableValue()
    {
        return sortableValue;
    }

    public void setSortableValue(String sortableValue)
    {
        this.sortableValue = sortableValue;
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
}
