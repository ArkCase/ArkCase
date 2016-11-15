package com.armedia.acm.form.plain.model;

/**
 * Created by riste.tutureski on 12/4/2015.
 */
public class PlainFormCreatedEvent extends PlainFormEvent
{
    private final String EVENT_TYPE = "com.armedia.acm.form.plain.created";

    public PlainFormCreatedEvent(PlainForm source, String formName, Long folderId, String cmisFolderId, String userId,
                                 String ipAddress, Long pdfRenditionId, Long xmlRenditionId)
    {
        super(source);

        setEventType(EVENT_TYPE);
        setFormName(formName);
        setFolderId(folderId);
        setCmisFolderId(cmisFolderId);
        setUserId(userId);
        setIpAddress(ipAddress);
        setPdfRenditionId(pdfRenditionId);
        setXmlRenditionId(xmlRenditionId);
    }
}
