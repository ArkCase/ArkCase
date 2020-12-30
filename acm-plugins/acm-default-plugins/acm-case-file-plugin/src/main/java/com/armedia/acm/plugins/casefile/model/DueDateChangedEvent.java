package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.core.model.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

public class DueDateChangedEvent extends AcmEvent
{

    private Authentication eventUser;
    private CaseFile caseFile;
    private Date oldDueDate;
    private Date newDueDate;

    private static final String EVENT_TYPE = "com.armedia.acm.casefile.due.date.changed";

    public DueDateChangedEvent(CaseFile source, Date oldDate, String ipAddress, String user, Date eventDate,
            boolean userActionSucceeded,
            Authentication eventUser)
    {
        super(source);

        setSucceeded(userActionSucceeded);
        setUserId(user);
        setObjectId(source.getId());
        setEventDate(eventDate);
        setIpAddress(ipAddress);
        setEventType(EVENT_TYPE);
        setObjectType("CASE_FILE");
        setEventUser(eventUser);
        setCaseFile(source);
        setOldDueDate(oldDate);
        setNewDueDate(source.getDueDate());
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

    public Date getOldDueDate()
    {
        return oldDueDate;
    }

    public void setOldDueDate(Date oldDueDate)
    {
        this.oldDueDate = oldDueDate;
    }

    public Date getNewDueDate()
    {
        return newDueDate;
    }

    public void setNewDueDate(Date newDueDate)
    {
        this.newDueDate = newDueDate;
    }
}
