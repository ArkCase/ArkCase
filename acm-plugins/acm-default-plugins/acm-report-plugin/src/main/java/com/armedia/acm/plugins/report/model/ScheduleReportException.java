package com.armedia.acm.plugins.report.model;

/**
 * Created by joseph.mcgrady on 7/10/2017.
 */
public class ScheduleReportException extends Exception
{
    private static final long serialVersionUID = 125792659892263L;

    public ScheduleReportException()
    {
    }

    public ScheduleReportException(String message)
    {
        super(message);
    }

    public ScheduleReportException(String message, Throwable cause)
    {
        super(message, cause);
    }
}