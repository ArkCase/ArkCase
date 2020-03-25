package gov.foia.model;

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
