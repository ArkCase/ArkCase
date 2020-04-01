package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

public class WithdrawRequest
{

    private String firstName;

    private String lastName;

    private String email;

    private String subject;

    private String description;

    private String ipAddress;

    private String userId;

    private String originalRequestNumber;

    private List<PortalFOIARequestFile> documents;

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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getOriginalRequestNumber()
    {
        return originalRequestNumber;
    }

    public void setOriginalRequestNumber(String originalRequestNumber)
    {
        this.originalRequestNumber = originalRequestNumber;
    }

    public List<PortalFOIARequestFile> getDocuments()
    {
        return documents;
    }

    public void setDocuments(List<PortalFOIARequestFile> documents)
    {
        this.documents = documents;
    }

    @Override
    public String toString()
    {
        return "WithdrawRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", title='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", userId='" + userId + '\'' +
                ", requestId='" + originalRequestNumber + '\'' +
                '}';
    }
}
