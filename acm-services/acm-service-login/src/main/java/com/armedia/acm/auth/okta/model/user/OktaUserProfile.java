package com.armedia.acm.auth.okta.model.user;

import com.fasterxml.jackson.annotation.JsonRootName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonRootName("profile")
public class OktaUserProfile
{
    private String login;
    private String firstName;
    private String lastName;
    private String nickname;
    private String displayName;
    private String email;
    private String secondEmail;
    private String profileUrl;
    private String preferredLanguage;
    private String userType;
    private String organization;
    private String title;
    private String division;
    private String department;
    private String costCenter;
    private String employeeNumber;
    private String mobilePhone;
    private String primaryPhone;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String countryCode;

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
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

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getSecondEmail()
    {
        return secondEmail;
    }

    public void setSecondEmail(String secondEmail)
    {
        this.secondEmail = secondEmail;
    }

    public String getProfileUrl()
    {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl)
    {
        this.profileUrl = profileUrl;
    }

    public String getPreferredLanguage()
    {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage)
    {
        this.preferredLanguage = preferredLanguage;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization(String organization)
    {
        this.organization = organization;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDivision()
    {
        return division;
    }

    public void setDivision(String division)
    {
        this.division = division;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter(String costCenter)
    {
        this.costCenter = costCenter;
    }

    public String getEmployeeNumber()
    {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber)
    {
        this.employeeNumber = employeeNumber;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getPrimaryPhone()
    {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone)
    {
        this.primaryPhone = primaryPhone;
    }

    public String getStreetAddress()
    {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("login", login)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("nickname", nickname)
                .append("displayName", displayName)
                .append("email", email)
                .append("secondEmail", secondEmail)
                .append("profileUrl", profileUrl)
                .append("preferredLanguage", preferredLanguage)
                .append("userType", userType)
                .append("organization", organization)
                .append("title", title)
                .append("division", division)
                .append("department", department)
                .append("costCenter", costCenter)
                .append("employeeNumber", employeeNumber)
                .append("mobilePhone", mobilePhone)
                .append("primaryPhone", primaryPhone)
                .append("streetAddress", streetAddress)
                .append("city", city)
                .append("state", state)
                .append("zipCode", zipCode)
                .append("countryCode", countryCode)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        OktaUserProfile that = (OktaUserProfile) o;

        return new EqualsBuilder()
                .append(login, that.login)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(nickname, that.nickname)
                .append(displayName, that.displayName)
                .append(email, that.email)
                .append(secondEmail, that.secondEmail)
                .append(profileUrl, that.profileUrl)
                .append(preferredLanguage, that.preferredLanguage)
                .append(userType, that.userType)
                .append(organization, that.organization)
                .append(title, that.title)
                .append(division, that.division)
                .append(department, that.department)
                .append(costCenter, that.costCenter)
                .append(employeeNumber, that.employeeNumber)
                .append(mobilePhone, that.mobilePhone)
                .append(primaryPhone, that.primaryPhone)
                .append(streetAddress, that.streetAddress)
                .append(city, that.city)
                .append(state, that.state)
                .append(zipCode, that.zipCode)
                .append(countryCode, that.countryCode)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(login)
                .append(firstName)
                .append(lastName)
                .append(nickname)
                .append(displayName)
                .append(email)
                .append(secondEmail)
                .append(profileUrl)
                .append(preferredLanguage)
                .append(userType)
                .append(organization)
                .append(title)
                .append(division)
                .append(department)
                .append(costCenter)
                .append(employeeNumber)
                .append(mobilePhone)
                .append(primaryPhone)
                .append(streetAddress)
                .append(city)
                .append(state)
                .append(zipCode)
                .append(countryCode)
                .toHashCode();
    }
}
