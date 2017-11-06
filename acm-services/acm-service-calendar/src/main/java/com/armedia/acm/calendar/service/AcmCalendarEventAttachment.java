package com.armedia.acm.calendar.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 31, 2017
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmCalendarEventAttachment
{

    private String fileName;

    private String attachmentId;

    private String eventId;

    public AcmCalendarEventAttachment()
    {
    }

    public AcmCalendarEventAttachment(String fileName, String attachmentId, String eventId)
    {
        this.fileName = fileName;
        this.attachmentId = attachmentId;
        this.eventId = eventId;
    }

    /**
     * @return the fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * @return the attachmentId
     */
    public String getAttachmentId()
    {
        return attachmentId;
    }

    /**
     * @param attachmentId
     *            the attachmentId to set
     */
    public void setAttachmentId(String attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    /**
     * @return the eventId
     */
    public String getEventId()
    {
        return eventId;
    }

    /**
     * @param eventId
     *            the eventId to set
     */
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

}
