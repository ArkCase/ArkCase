package com.armedia.acm.form.plain.model;

/**
 * Created by riste.tutureski on 12/4/2015.
 */
public class PlainFormCreatedEvent extends PlainFormEvent
{
    private final String EVENT_TYPE = "com.armedia.acm.form.plain.created";
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never {@code null})
     */
    public PlainFormCreatedEvent(PlainForm source, String formName, Long folderId, String cmisFolderId, String userId, String ipAddress)
    {
        super(source);

        setEventType(EVENT_TYPE);
        setFormName(formName);
        setFolderId(folderId);
        setCmisFolderId(cmisFolderId);
        setUserId(userId);
        setIpAddress(ipAddress);
    }
}
