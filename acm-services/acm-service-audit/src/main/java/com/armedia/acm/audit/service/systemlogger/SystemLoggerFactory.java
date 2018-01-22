package com.armedia.acm.audit.service.systemlogger;

/**
 * Created by Bojan Milenkoski on 29.12.2015.
 */
public class SystemLoggerFactory
{
    private ISystemLogger syslogLogger;
    private ISystemLogger windowsEventLogger;

    public ISystemLogger getSystemLogger()
    {
        if (System.getProperty("os.name").startsWith("Windows"))
        {
            return windowsEventLogger;
        }
        else
        {
            return syslogLogger;
        }
    }

    public ISystemLogger getWindowsEventLogger()
    {
        return windowsEventLogger;
    }

    public void setWindowsEventLogger(ISystemLogger windowsEventLogger)
    {
        this.windowsEventLogger = windowsEventLogger;
    }

    public ISystemLogger getSyslogLogger()
    {
        return syslogLogger;
    }

    public void setSyslogLogger(ISystemLogger syslogLogger)
    {
        this.syslogLogger = syslogLogger;
    }
}
