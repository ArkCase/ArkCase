package com.armedia.acm.plugins.report.web.api;

/*-
 * #%L
 * ACM Default Plugin: report
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.pentaho.config.PentahoReportsConfig;
import com.armedia.acm.plugins.report.model.PentahoReportSchedule;
import com.armedia.acm.plugins.report.model.PentahoScheduleRequest;
import com.armedia.acm.plugins.report.model.ScheduleReportException;
import com.armedia.acm.plugins.report.service.ScheduleReportService;

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dwu on 6/13/2017.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class ScheduleReportAPIController
{
    private static final Logger LOGGER = LogManager.getLogger(ScheduleReportAPIController.class);
    private ScheduleReportService scheduleReportService;
    private PentahoReportsConfig pentahoReportsConfig;

    @RequestMapping(value = "/schedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> processReportSchedule(@RequestBody PentahoReportSchedule pentahoReportSchedule)
            throws ScheduleReportException
    {

        JSONObject retval = new JSONObject();

        if (pentahoReportSchedule == null || pentahoReportSchedule.getUiPassParam() == null)
        {
            LOGGER.error("Invalid input given or missing schedule type: DAILY, WEEKLY, etc.");
            retval.put("error", "invalidScheduleTime");
            return new ResponseEntity<>(retval.toString(), HttpStatus.BAD_REQUEST);
        }

        try
        {
            if (!pentahoReportSchedule.getEndTime().isEmpty())
            {
                Date startTime = convertTime(pentahoReportSchedule.getStartTime());
                Date endTime = convertTime(pentahoReportSchedule.getEndTime());
                if (startTime.after(endTime))
                {
                    LOGGER.error(pentahoReportSchedule.getEndTime() + "(End time) cannot be before "
                            + pentahoReportSchedule.getStartTime() + "(Start time)");
                    retval.put("error", "beforeTimeError");
                    return new ResponseEntity<>(retval.toString(), HttpStatus.BAD_REQUEST);
                }
            }
        }
        catch (ParseException e)
        {
            LOGGER.error("Error due parsing the start or end time (" + pentahoReportSchedule.getStartTime() + ", " +
                    pentahoReportSchedule.getEndTime() + ")", e);
            retval.put("error", "parsingError");
            return new ResponseEntity<>(retval.toString(), HttpStatus.BAD_REQUEST);
        }

        PentahoScheduleRequest pentahoScheduleRequest = new PentahoScheduleRequest(pentahoReportSchedule.getUiPassParam(),
                pentahoReportSchedule.getStartTime(),
                pentahoReportSchedule.getEndTime(), pentahoReportSchedule.getJobName(), pentahoReportSchedule.getEmails(),
                pentahoReportSchedule.getFilterStartDate(), pentahoReportSchedule.getFilterEndDate(), pentahoReportSchedule.getReportFile(),
                pentahoReportsConfig.getScheduleInputFolder(), pentahoReportsConfig.getScheduleOutputFolder());
        getScheduleReportService().scheduleReport(pentahoScheduleRequest.toJSONSting());

        LOGGER.debug(pentahoReportSchedule.toString());
        LOGGER.debug(pentahoScheduleRequest.toJSONSting());
        return new ResponseEntity<>(retval.toString(), HttpStatus.OK);
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

    private Date convertTime(String date) throws ParseException
    {
        Date formattedDate = new SimpleDateFormat(DateFormats.DEFAULT_DATE_TIME_FORMAT).parse(date);
        return formattedDate;
    }

    public PentahoReportsConfig getPentahoReportsConfig()
    {
        return pentahoReportsConfig;
    }

    public void setPentahoReportsConfig(PentahoReportsConfig pentahoReportsConfig)
    {
        this.pentahoReportsConfig = pentahoReportsConfig;
    }
}
