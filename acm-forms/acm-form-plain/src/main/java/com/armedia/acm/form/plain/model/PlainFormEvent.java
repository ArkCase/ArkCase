package com.armedia.acm.form.plain.model;

import org.springframework.context.ApplicationEvent;

/**
 * Created by riste.tutureski on 12/4/2015.
 */
public class PlainFormEvent extends ApplicationEvent
{
    private String eventType;
    private String formName;
    private Long folderId;
    private String cmisFolderId;
    private String userId;
    private String ipAddress;
    private Long pdfRenditionId;
    private Long xmlRenditionId;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never {@code null})
     */
    public PlainFormEvent(PlainForm source)
    {
        super(source);
    }

    public String getFormName()
    {
        return formName;
    }

    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    public Long getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Long folderId)
    {
        this.folderId = folderId;
    }

    public String getCmisFolderId()
    {
        return cmisFolderId;
    }

    public void setCmisFolderId(String cmisFolderId)
    {
        this.cmisFolderId = cmisFolderId;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Long getPdfRenditionId()
    {
        return pdfRenditionId;
    }

    public void setPdfRenditionId(Long pdfRenditionId)
    {
        this.pdfRenditionId = pdfRenditionId;
    }

    public Long getXmlRenditionId()
    {
        return xmlRenditionId;
    }

    public void setXmlRenditionId(Long xmlRenditionId)
    {
        this.xmlRenditionId = xmlRenditionId;
    }
}
