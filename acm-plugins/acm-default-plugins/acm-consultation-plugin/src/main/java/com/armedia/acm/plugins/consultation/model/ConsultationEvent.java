package com.armedia.acm.plugins.consultation.model;

import com.armedia.acm.core.model.AcmEvent;

import org.springframework.security.core.Authentication;

import java.util.Date;

public class ConsultationEvent extends AcmEvent
{
    private Authentication eventUser;
    private Consultation consultation;

    public ConsultationEvent(Consultation source, String ipAddress, String user, String eventType, Date eventDate, boolean userActionSucceeded,
                     Authentication eventUser)
    {
        super(source);

        setSucceeded(userActionSucceeded);
        setUserId(user);
        setObjectId(source.getId());
        setEventDate(eventDate);
        setIpAddress(ipAddress);
        setEventType(eventType);
        setObjectType(ConsultationConstants.OBJECT_TYPE);
        setEventUser(eventUser);
        setConsultation(source);
    }

    public ConsultationEvent(Consultation source, String ipAddress, String user, String eventType, String eventDescription, Date eventDate,
                     boolean userActionSucceeded, Authentication eventUser)
    {
        this(source, ipAddress, user, eventType, eventDate, userActionSucceeded, eventUser);
        setEventDescription(eventDescription);
    }

    public Authentication getEventUser()
    {
        return eventUser;
    }

    public void setEventUser(Authentication eventUser)
    {
        this.eventUser = eventUser;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }
}