package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.event.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 9/4/14.
 */
public class CaseEvent extends AcmEvent
{
    private Authentication eventUser;
    private CaseFile caseFile;

    public CaseEvent(
            CaseFile source,
            String ipAddress,
            String user,
            String eventType,
            Date eventDate,
            boolean userActionSucceeded,
            Authentication eventUser)
    {
        super(source);

        setSucceeded(userActionSucceeded);
        setUserId(user);
        setObjectId(source.getId());
        setEventDate(eventDate);
        setIpAddress(ipAddress);
        setEventType(eventType);
        setObjectType("CASE_FILE");
        setEventUser(eventUser);
        setCaseFile(source);
    }

    public Authentication getEventUser()
    {
        return eventUser;
    }

    public void setEventUser(Authentication eventUser)
    {
        this.eventUser = eventUser;
    }

    public CaseFile getCaseFile()
    {
        return caseFile;
    }

    public void setCaseFile(CaseFile caseFile)
    {
        this.caseFile = caseFile;
    }
}
