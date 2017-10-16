package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;

import java.time.LocalDate;
import java.util.Objects;

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

    public AcmUser toAcmUser()
    {
        AcmUser acmUser = new AcmUser();
        acmUser.setUserId(userId.toLowerCase());
        acmUser.setDistinguishedName(distinguishedName);
        acmUser.setUid(uid);
        acmUser.setUserPrincipalName(userPrincipalName);
        acmUser.setsAMAccountName(sAMAccountName);
        acmUser.setUserDirectoryName(directoryName);
        return setAcmUserEditableFields(acmUser);
    }

    public AcmUser setAcmUserEditableFields(AcmUser acmUser)
    {
        acmUser.setFirstName(firstName);
        acmUser.setLastName(lastName);
        acmUser.setUserState(AcmUserState.valueOf(state));
        acmUser.setMail(mail);
        acmUser.setFullName(fullName);
        acmUser.setCompany(company);
        acmUser.setCountry(country);
        acmUser.setCountryAbbreviation(countryAbbreviation);
        acmUser.setPasswordExpirationDate(passwordExpirationDate);
        return acmUser;
    }

    public boolean isChanged(AcmUser acmUser)
    {
        return !(Objects.equals(directoryName, acmUser.getUserDirectoryName()) &&
                Objects.equals(state, acmUser.getUserState().name()) &&
                Objects.equals(title, acmUser.getTitle()) &&
                Objects.equals(mail, acmUser.getMail()) &&
                Objects.equals(firstName, acmUser.getFirstName()) &&
                Objects.equals(lastName, acmUser.getLastName()) &&
                Objects.equals(company, acmUser.getCompany()) &&
                Objects.equals(country, acmUser.getCountry()) &&
                Objects.equals(countryAbbreviation, acmUser.getCountryAbbreviation()) &&
                Objects.equals(department, acmUser.getDepartment()) &&
                Objects.equals(passwordExpirationDate, acmUser.getPasswordExpirationDate()));
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
