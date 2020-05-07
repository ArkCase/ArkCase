package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import gov.foia.util.JsonDateSerializer;

/**
 * This class represents an HTML form request from an external port site. The HTML form fields must have the same names
 * as the field names in this class.
 */
public class PortalFOIARequestStatus implements Serializable
{

    private static final long serialVersionUID = 7561379926952737189L;

    private String requestId;

    private String lastName;

    private String requestStatus;

    private String queue;

    private Boolean isDenied;

    private Boolean isPublic;

    private Boolean withdrawRequested;

    private String requestType;

    private String requesterFirstName;

    private String requesterLastName;

    private String requesterEmail;

    private String dispositionValue;

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

    public Boolean getIsDenied()
    {
        return isDenied;
    }

    public void setIsDenied(Boolean denied)
    {
        isDenied = denied;
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

    public String getDispositionValue() { return dispositionValue; }

    public void setDispositionValue(String dispositionValue) { this.dispositionValue = dispositionValue; }

    public Boolean getWithdrawRequested()
    {
        return withdrawRequested;
    }

    public void setWithdrawRequested(Boolean withdrawRequested)
    {
        this.withdrawRequested = withdrawRequested;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "PortalFOIARequestStatus [requestId=" + requestId + ", lastName=" + lastName + ", requestType=" + requestType
                + ", requestStatus=" + requestStatus + ", queue=" + queue + ", isDenied=" + isDenied
                + ", isPublic=" + isPublic + ", updateDate=" + updateDate + ", requesterFirstName=" + requesterFirstName
                + ", requesterLastName=" + requesterLastName + ", requesterEmail+" + requesterEmail + ", dispositionValue+" + dispositionValue + "]";
    }
}
