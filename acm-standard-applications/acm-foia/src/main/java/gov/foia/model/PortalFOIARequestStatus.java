package gov.foia.model;

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

    private Boolean isPublic;

    private String requestType;

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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "PortalFOIARequestStatus [requestId=" + requestId + ", lastName=" + lastName + ", requestType=" + requestType
                + ", requestStatus=" + requestStatus
                + ", isPublic=" + isPublic + ", updateDate=" + updateDate + "]";
    }

}
