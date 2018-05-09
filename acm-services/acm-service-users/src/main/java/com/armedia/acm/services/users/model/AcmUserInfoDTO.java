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

import java.util.List;
import java.util.Set;

/**
 * Created by armdev on 8/6/15.
 */
public class AcmUserInfoDTO
{
    private String userId;
    private String fullName;
    private String mail;
    private String firstName;
    private String lastName;
    private List<String> authorities;
    private Set<String> privileges;
    private String directoryName;
    private String country;
    private String countryAbbreviation;
    private String department;
    private String company;
    private String title;
    private String notificationMessage;
    private String langCode;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
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

    public List<String> getAuthorities()
    {
        return authorities;
    }

    public void setAuthorities(List<String> authorities)
    {
        this.authorities = authorities;
    }

    public Set<String> getPrivileges()
    {
        return privileges;
    }

    public void setPrivileges(Set<String> privileges)
    {
        this.privileges = privileges;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
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

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getNotificationMessage()
    {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage)
    {
        this.notificationMessage = notificationMessage;
    }

    public String getLangCode()
    {
        return langCode;
    }

    public void setLangCode(String lang)
    {
        this.langCode = lang;
    }
}
