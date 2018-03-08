package com.armedia.acm.plugins.report.service;

import com.armedia.acm.plugins.report.model.ScheduleReportException;

/**
 * Created by dwu on 6/9/2017.
 */
public interface ScheduleReportService
{
    void scheduleReport(String jsonString) throws ScheduleReportException;

    String retrieveSchedules() throws ScheduleReportException;

    String deleteSchedule(String scheduleId) throws ScheduleReportException;
}
