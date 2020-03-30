package gov.foia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortalFOIAInquiry {
    private String parentId;
    private String description;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String subject;
    private List<PortalFOIARequestFile> documents;
    private String userId;

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public List<PortalFOIARequestFile> getDocuments()
    {
        return documents;
    }

    public void setDocuments(List<PortalFOIARequestFile> documents)
    {
        this.documents = documents;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "PortalFOIAInquiry [parentId=" + parentId + ", description=" + description + ", firstName=" + firstName
                + ", lastName=" + lastName + ", emailAddress=" + emailAddress + ", subject=" + subject + ", documents="
                + documents + ", userId=" + userId + "]";
    }
}
