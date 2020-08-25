package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import gov.privacy.util.JsonDateSerializer;

/**
 * This class represents an HTML form request from an external port site. The HTML form fields must have the same names
 * as the field names in this class.
 *
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 */
public class PortalSARStatus implements Serializable
{

    private static final long serialVersionUID = 7561379926952737189L;

    private String requestId;

    private String originalRequestId;

    private String requestTitle;

    private String lastName;

    private String requestStatus;

    private String queue;

    private Boolean isPublic;

    private String requestType;

    private String requesterFirstName;

    private String requesterLastName;

    private String requesterEmail;

    private String subjectFirstName;

    private String subjectLastName;

    private String subjectEmail;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date updateDate;

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getRequestStatus()
    {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus)
    {
        this.requestStatus = requestStatus;
    }

    /**
     * @return the isPublic
     */
    public Boolean getIsPublic()
    {
        return isPublic;
    }

    /**
     * @param isPublic
     *            the isPublic to set
     */
    public void setIsPublic(Boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public String getRequestType()
    {
        return requestType;
    }

    public void setRequestType(String requestType)
    {
        this.requestType = requestType;
    }

    public String getQueue()
    {
        return queue;
    }

    public void setQueue(String queue)
    {
        this.queue = queue;
    }

    public String getRequesterFirstName()
    {
        return requesterFirstName;
    }

    public void setRequesterFirstName(String requesterFirstName)
    {
        this.requesterFirstName = requesterFirstName;
    }

    public String getRequesterLastName()
    {
        return requesterLastName;
    }

    public void setRequesterLastName(String requesterLastName)
    {
        this.requesterLastName = requesterLastName;
    }

    public String getRequesterEmail()
    {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail)
    {
        this.requesterEmail = requesterEmail;
    }

    public String getRequestTitle()
    {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle)
    {
        this.requestTitle = requestTitle;
    }

    public String getOriginalRequestId()
    {
        return originalRequestId;
    }

    public void setOriginalRequestId(String originalRequestId)
    {
        this.originalRequestId = originalRequestId;
    }

    public String getSubjectFirstName()
    {
        return subjectFirstName;
    }

    public void setSubjectFirstName(String subjectFirstName)
    {
        this.subjectFirstName = subjectFirstName;
    }

    public String getSubjectLastName()
    {
        return subjectLastName;
    }

    public void setSubjectLastName(String subjectLastName)
    {
        this.subjectLastName = subjectLastName;
    }

    public String getSubjectEmail()
    {
        return subjectEmail;
    }

    public void setSubjectEmail(String subjectEmail)
    {
        this.subjectEmail = subjectEmail;
    }

    @Override
    public String toString()
    {
        return "PortalSARStatus{" +
                "requestId='" + requestId + '\'' +
                ", originalRequestId='" + originalRequestId + '\'' +
                ", requestTitle='" + requestTitle + '\'' +
                ", lastName='" + lastName + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", queue='" + queue + '\'' +
                ", isPublic=" + isPublic +
                ", requestType='" + requestType + '\'' +
                ", requesterFirstName='" + requesterFirstName + '\'' +
                ", requesterLastName='" + requesterLastName + '\'' +
                ", requesterEmail='" + requesterEmail + '\'' +
                ", subjectFirstName='" + subjectFirstName + '\'' +
                ", subjectLastName='" + subjectLastName + '\'' +
                ", subjectEmail='" + subjectEmail + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}
