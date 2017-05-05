package com.armedia.acm.service.outlook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailWithEmbeddedLinksDTO extends MessageBodyFactory
{

    private String subject;

    private String title;

    private String header;

    private String footer;

    private List<String> emailAddresses;

    private List<Long> fileIds;

    private String baseUrl;

    private String body;

    public String buildMessageBodyFromTemplate(String body)
    {
        return buildMessageBodyFromTemplate(body, getHeader(), getFooter());
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public List<String> getEmailAddresses()
    {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses)
    {
        this.emailAddresses = emailAddresses;
    }

    public List<Long> getFileIds()
    {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds)
    {
        this.fileIds = fileIds;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
}
