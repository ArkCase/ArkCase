package com.armedia.acm.audit.service.systemlogger;

import com.armedia.acm.core.AcmApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Windows event logger implementation of {@link ISystemLogger}.
 * To create the app source in event log open terminal as Administrator and run:
 * eventcreate /l APPLICATION /so "applicationName defined in app-config.xml" /t Information /id 1 /d "Creating app
 * source"
 * EventID must be in the range of 1 - 1000.
 * <p>
 * Created by Bojan Milenkoski on 28.12.2015.
 */
public class WindowsEventLogger implements ISystemLogger
{
    private static final String level = "Information";
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmApplication acmApplication;
    private int eventId;

    @Override
    public void log(String message)
    {
        String command = "eventcreate "
                + " /l APPLICATION"
                + " /so \"" + getAcmApplication().getApplicationName() + "\""
                + " /t " + level
                + " /id " + getEventId()
                + " /d \"" + message + "\"";

        try
        {
            Runtime.getRuntime().exec(command);
        }
        catch (IOException e)
        {
            log.error("Error writing to Windows Event Log!", e);
        }
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }

    public int getEventId()
    {
        return eventId;
    }

    public void setEventId(int eventId)
    {
        this.eventId = eventId;
    }
}
