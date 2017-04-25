package com.armedia.acm.service.outlook.model;

import java.util.List;

/**
 * Created by manoj.dhungana on 7/28/2015.
 */
public class EmailWithAttachmentsDTO extends MessageBodyFactory
{

    private List<Long> attachmentIds;
    private List<String> filePaths;
    private String objectType;
    private Long objectId;
    private String subject;
    private String header;
    private String footer;
    private String body;
    private List<String> users;
    private List<String> emailAddresses;

    public List<Long> getAttachmentIds()
    {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Long> attachmentIds)
    {
        this.attachmentIds = attachmentIds;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public List<String> getUsers()
    {
        return users;
    }

    public void setUsers(List<String> users)
    {
        this.users = users;
    }

    public List<String> getEmailAddresses()
    {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses)
    {
        this.emailAddresses = emailAddresses;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getFooter()
    {
        return footer;
    }

    public void setFooter(String footer)
    {
        this.footer = footer;
    }

    public String getMessageBody()
    {
        return buildMessageBodyFromTemplate(getBody(), getHeader(), getFooter());
    }

    public List<String> getFilePaths()
    {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths)
    {
        this.filePaths = filePaths;
    }

}
