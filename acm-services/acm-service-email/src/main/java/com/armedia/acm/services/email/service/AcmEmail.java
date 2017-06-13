package com.armedia.acm.services.email.service;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public class AcmEmail
{

    public static enum EmailAs
    {
        ATTACHMENT, LINK, PLAIN;
    }

    private Long objectId;

    private String objectType;

    private String subject;

    private List<String> recipients;

    private String body;

    private List<String> documentIds;

    private EmailAs emailAs;

    /**
     * @return the objectId
     */
    public Long getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the subject
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * @param subject
     *            the subject to set
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * @return the recipients
     */
    public List<String> getRecipients()
    {
        return recipients;
    }

    /**
     * @param recipients
     *            the recipients to set
     */
    public void setRecipients(List<String> recipients)
    {
        this.recipients = recipients;
    }

    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    /**
     * @param body
     *            the body to set
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * @return the documentIds
     */
    public List<String> getDocumentIds()
    {
        return documentIds;
    }

    /**
     * @param documentIds
     *            the documentIds to set
     */
    public void setDocumentIds(List<String> documentIds)
    {
        this.documentIds = documentIds;
    }

    /**
     * @return the emailAs
     */
    public EmailAs getEmailAs()
    {
        return emailAs;
    }

    /**
     * @param emailAs
     *            the emailAs to set
     */
    public void setEmailAs(EmailAs emailAs)
    {
        this.emailAs = emailAs;
    }

}
