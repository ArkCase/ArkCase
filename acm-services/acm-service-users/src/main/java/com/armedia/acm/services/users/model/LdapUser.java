package com.armedia.acm.services.users.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LdapUser
{
    private String userId;
    private String distinguishedName;
    private String directoryName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String uid;
    private String sAMAccountName;
    private String mail;
    private String sortableValue;
    private String country;
    private String countryAbbreviation;
    private String company;
    private String department;
    private String title;
    private String userPrincipalName;
    private String state;
    private LocalDate passwordExpirationDate;
    private Set<String> ldapGroups = new HashSet<>();

    public AcmUser toAcmUser()
    {
        AcmUser acmUser = new AcmUser();
        acmUser.setUserId(getUserId());
        acmUser.setDistinguishedName(getDistinguishedName());
        acmUser.setUid(getUid());
        acmUser.setUserPrincipalName(getUserPrincipalName());
        acmUser.setsAMAccountName(getsAMAccountName());
        acmUser.setUserDirectoryName(getDirectoryName());
        return setAcmUserEditableFields(acmUser);
    }

    public AcmUser setAcmUserEditableFields(AcmUser acmUser)
    {
        acmUser.setFirstName(getFirstName());
        acmUser.setLastName(getLastName());
        acmUser.setUserState(getState());
        acmUser.setMail(getMail());
        acmUser.setFullName(getFullName());
        acmUser.setCompany(getCompany());
        acmUser.setCountry(getCountry());
        acmUser.setCountryAbbreviation(getCountryAbbreviation());
        acmUser.setUserState(getState());
        acmUser.setPasswordExpirationDate(getPasswordExpirationDate());
        return acmUser;
    }

    public boolean isChanged(AcmUser acmUser)
    {
        return !(Objects.equals(getDirectoryName(), acmUser.getUserDirectoryName()) &&
                Objects.equals(getState(), acmUser.getUserState()) &&
                Objects.equals(getTitle(), acmUser.getTitle()) &&
                Objects.equals(getMail(), acmUser.getMail()) &&
                Objects.equals(getFirstName(), acmUser.getFirstName()) &&
                Objects.equals(getLastName(), acmUser.getLastName()) &&
                Objects.equals(getCompany(), acmUser.getCompany()) &&
                Objects.equals(getCountry(), acmUser.getCountry()) &&
                Objects.equals(getCountryAbbreviation(), acmUser.getCountryAbbreviation()) &&
                Objects.equals(getDepartment(), acmUser.getDepartment()) &&
                Objects.equals(getPasswordExpirationDate(), acmUser.getPasswordExpirationDate()));
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

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

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getsAMAccountName()
    {
        return sAMAccountName;
    }

    public void setsAMAccountName(String sAMAccountName)
    {
        this.sAMAccountName = sAMAccountName;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

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

    public String getUserPrincipalName()
    {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName)
    {
        this.userPrincipalName = userPrincipalName;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public LocalDate getPasswordExpirationDate()
    {
        return passwordExpirationDate;
    }

    public void setPasswordExpirationDate(LocalDate passwordExpirationDate)
    {
        this.passwordExpirationDate = passwordExpirationDate;
    }

    public Set<String> getLdapGroups()
    {
        return ldapGroups;
    }

    public void setLdapGroups(Set<String> ldapGroups)
    {
        this.ldapGroups = ldapGroups;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdapUser ldapUser = (LdapUser) o;
        return Objects.equals(userId, ldapUser.userId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId);
    }
}
