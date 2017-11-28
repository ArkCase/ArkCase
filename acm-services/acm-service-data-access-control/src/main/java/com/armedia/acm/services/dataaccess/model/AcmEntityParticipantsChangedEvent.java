package com.armedia.acm.services.dataaccess.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.MDC;

import java.util.Date;
import java.util.List;

public class AcmEntityParticipantsChangedEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;

    public static final String EVENT_TYPE = "com.armedia.acm.object.participants.change";

    private List<AcmParticipant> originalParticipants;

    public AcmEntityParticipantsChangedEvent(AcmObject source, List<AcmParticipant> originalParticipants)
    {
        super(source);
        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) == null ? "SYSTEM"
                : MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
        setSucceeded(true);
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setOriginalParticipants(originalParticipants);
    }

    public List<AcmParticipant> getOriginalParticipants()
    {
        return originalParticipants;
    }

    public void setOriginalParticipants(List<AcmParticipant> originalParticipants)
    {
        this.originalParticipants = originalParticipants;
    }
}
