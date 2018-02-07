package com.armedia.acm.plugins.report.web.api;

import com.armedia.acm.plugins.report.model.ReportScheduler;
import com.armedia.acm.plugins.report.model.ScheduleReportException;
import com.armedia.acm.plugins.report.model.ScheduleRequest;
import com.armedia.acm.plugins.report.service.ScheduleReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dwu on 6/13/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/report", "/api/latest/plugin/report"})
public class ScheduleReportAPIController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleReportAPIController.class);
    private ScheduleReportService scheduleReportService;
    private String scheduleInputFolder;
    private String scheduleOutputFolder;

    @RequestMapping(value = "/schedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void processReportSchedule(@RequestBody ReportScheduler reportSchedule)
            throws ScheduleReportException
    {

        if (reportSchedule == null || reportSchedule.getUiPassParam() == null)
        {
            throw new ScheduleReportException("Invalid input given or missing schedule type: DAILY, WEEKLY, etc.");
        }

        ScheduleRequest scheduleRequest = new ScheduleRequest(reportSchedule.getUiPassParam(), reportSchedule.getStartTime(),
                reportSchedule.getEndTime(), reportSchedule.getJobName(), reportSchedule.getEmails(),
                reportSchedule.getFilterStartDate(), reportSchedule.getFilterEndDate(), reportSchedule.getReportFile(), scheduleInputFolder, scheduleOutputFolder);
        getScheduleReportService().scheduleReport(scheduleRequest.toJSONSting());

        LOGGER.debug(reportSchedule.toString());
        LOGGER.debug(scheduleRequest.toJSONSting());
    }

    @RequestMapping(value = "/schedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String retrieveSchedules() throws ScheduleReportException
    {
        LOGGER.info("Retrieving the list of existing report schedules");

        String schedules = getScheduleReportService().retrieveSchedules();
        LOGGER.debug("Report Schedules found: [{}]", schedules);
        return schedules;
    }

    @RequestMapping(value = "/schedule/byId", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String deleteSchedule(@RequestParam("id") String scheduleId)
            throws ScheduleReportException
    {
        LOGGER.info("Deleting schedule [{}]", scheduleId);

        String response = getScheduleReportService().deleteSchedule(scheduleId);
        LOGGER.debug("Delete result: [{}]", response);
        return response;
    }

    public ScheduleReportService getScheduleReportService()
    {
        return scheduleReportService;
    }

    public void setScheduleReportService(ScheduleReportService scheduleReportService)
    {
        this.scheduleReportService = scheduleReportService;
    }

    public String getScheduleInputFolder()
    {
        return scheduleInputFolder;
    }

    public void setScheduleInputFolder(String scheduleInputFolder)
    {
        this.scheduleInputFolder = scheduleInputFolder;
    }

    public String getScheduleOutputFolder()
    {
        return scheduleOutputFolder;
    }

    public void setScheduleOutputFolder(String scheduleOutputFolder)
    {
        this.scheduleOutputFolder = scheduleOutputFolder;
    }
}
