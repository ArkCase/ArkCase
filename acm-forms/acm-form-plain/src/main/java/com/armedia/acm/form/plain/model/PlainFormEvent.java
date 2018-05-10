package com.armedia.acm.form.plain.model;

/*-
 * #%L
 * ACM Forms: Plain
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
     * @param source
     *            the component that published the event (never {@code null})
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
