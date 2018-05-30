package com.armedia.acm.plugins.report.model;

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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Calendar;

/**
 * Created by dwu on 6/9/2017.
 * Pentaho scheduler JSON structure
 * JSONObject
 * jobName
 * JSONObject: simpleJobTrigger
 * uiPassParam
 * repeatInterval
 * repeatCount
 * startTime
 * endTime
 * inputFile
 * OutputFile
 * JSONArray: jobParmaters
 * output-target
 * accept-page
 * ......
 * <p>
 * {
 * "jobName":"MasterList",
 * "simpleJobTrigger":{"uiPassParam":"RUN_ONCE", "repeatInterval":0, "repeatCount":0,
 * "startTime":"2017-06-06T15:59:00.000-04:00", "endTime":null},
 * "inputFile":"/public/arkcase/MasterList.prpt",
 * "outputFile":"/public/admin",
 * "jobParameters":[
 * {"name":"output-target", "stringValue":["table/excel;page-mode=flow"],"type":"string"},
 * {"name":"accepted-page", "stringValue":["-1"], "type":"number"},
 * {"name":"::session", "stringValue":["d37c951f-4ae6-11e7-9f8c-005056a05782"], "type":"string"},
 * {"name":"showParameters", "stringValue":["true"], "type":"string"},
 * {"name":"renderMode", "stringValue":["XML"], "type":"string"},
 * {"name":"htmlProportionalWidth", "stringValue":["false"], "type":"string"},
 * {"name":"_SCH_EMAIL_TO", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
 * {"name":"_SCH_EMAIL_CC", "stringValue":"", "type":"string"},
 * {"name":"_SCH_EMAIL_BCC", "stringValue":"", "type":"string"},
 * {"name":"_SCH_EMAIL_SUBJECT", "stringValue":"MasterList schedule has successfully run.", "type":"string"},
 * {"name":"_SCH_EMAIL_MESSAGE", "stringValue":"this is a scheduled job", "type":"string"},
 * {"name":"_SCH_EMAIL_ATTACHMENT_NAME", "stringValue":"MasterList-20170606", "type":"string"}]
 * }
 */
public class PentahoScheduleRequest
{
    private JSONObject scheduleJob;

    private JSONObject jobTrigger;
    private String triggerName;

    private JSONArray jobParameters;
    private DateTimeFormatter dateTimeFormatter;
    private DateTime dateTime;
    private Calendar calendar;

    public PentahoScheduleRequest(String scheduleType, String startTime, String endTime, String jobName, String emails,
            String filterStartDate, String filterEndDate, String reportFile, String inputPath, String outputPath)
    {
        dateConversion(startTime);
        scheduleJob = new JSONObject();
        scheduleJob.put(PentahoReportScheduleConstants.JOB_NAME, jobName);

        setTrigger(scheduleType, startTime, endTime);

        scheduleJob.put(PentahoReportScheduleConstants.INPUT_FILE, buildFullPentahoPath(inputPath, reportFile));
        scheduleJob.put(PentahoReportScheduleConstants.OUTPUT_FILE, outputPath);

        setJobParameters(emails, filterStartDate, filterEndDate, jobName);
    }

    private String buildFullPentahoPath(String base, String path)
    {
        String fullPath = null;
        if (base != null && path != null)
        {
            if (base.endsWith("/"))
            {
                fullPath = base + path;
            }
            else
            {
                fullPath = base + "/" + path;
            }
        }
        return fullPath;
    }

    private void dateConversion(String startTime)
    {
        dateTimeFormatter = ISODateTimeFormat.dateTime();
        dateTime = dateTimeFormatter.parseDateTime(startTime);
        calendar = Calendar.getInstance();
        calendar.setTime(dateTime.toDate());
    }

    private void setTrigger(String scheduleType, String startTime, String endTime)
    {
        jobTrigger = new JSONObject();
        jobTrigger.put(PentahoReportScheduleConstants.UI_PASS_PARAM, scheduleType);

        setRepeatInterval(scheduleType);

        jobTrigger.put(PentahoReportScheduleConstants.START_TIME, startTime);
        jobTrigger.put(PentahoReportScheduleConstants.END_TIME, endTime);

        if (scheduleType.equals(PentahoReportScheduleConstants.RUN_ONCE) || scheduleType.equals(PentahoReportScheduleConstants.DAILY))
        {
            triggerName = PentahoReportScheduleConstants.SIMPLE_JOB_TRIGGER;
        }
        else
        {
            triggerName = PentahoReportScheduleConstants.COMPLEX_JOB_TRIGGER;
        }

        scheduleJob.put(triggerName, jobTrigger);
    }

    private void setRepeatInterval(String scheduleType)
    {
        if (scheduleType.equals(PentahoReportScheduleConstants.DAILY))
        {
            jobTrigger.put(PentahoReportScheduleConstants.REPEAT_INTERVAL, PentahoReportScheduleConstants.DAILY_INTERVAL);
            jobTrigger.put(PentahoReportScheduleConstants.REPEAT_COUNT, "-1");

        }
        else if (scheduleType.equals(PentahoReportScheduleConstants.WEEKLY))
        {
            // Pentaho dayOfWeek starts from 0 - Sunday, thus - 1.
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek > 0)
            {
                dayOfWeek = dayOfWeek - 1;
            }
            JSONArray dayOfWeekArray = new JSONArray();
            dayOfWeekArray.add(Integer.toString(dayOfWeek));
            jobTrigger.put(PentahoReportScheduleConstants.DAYS_OF_WEEK, dayOfWeekArray);
        }
        else if (scheduleType.equals(PentahoReportScheduleConstants.MONTHLY))
        {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            JSONArray dayOfMonthArray = new JSONArray();
            dayOfMonthArray.add(Integer.toString(dayOfMonth));
            jobTrigger.put(PentahoReportScheduleConstants.DAYS_OF_MONTH, dayOfMonthArray);
        }
        else
        {
            // RUN_ONCE
            jobTrigger.put(PentahoReportScheduleConstants.REPEAT_COUNT, 0);
            jobTrigger.put(PentahoReportScheduleConstants.REPEAT_INTERVAL, 0);
        }
    }

    public void setJobParameters(String emailTo, String filterStartDate, String filterEndDate, String jobName)
    {
        jobParameters = new JSONArray();

        jobParameters.add(buildParameter(PentahoReportScheduleConstants.OUTPUT_TARGET,
                PentahoReportScheduleConstants.OUTPUT_TARGET_STRING_VALUE,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.ACCEPTED_PAGE,
                PentahoReportScheduleConstants.ACCEPTED_PAGE_STRING_VALUE,
                PentahoReportScheduleConstants.NUMBER));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.START_DATE, filterStartDate,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.END_DATE, filterEndDate,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.DATE_FORMAT,
                PentahoReportScheduleConstants.REPORT_DATE_FORMAT,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.TIME_ZONE,
                PentahoReportScheduleConstants.UTC,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.STATUS,
                PentahoReportScheduleConstants.WILDCARD,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.SHOW_PARAMETERS,
                PentahoReportScheduleConstants.TRUE,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.RENDER_MODE,
                PentahoReportScheduleConstants.XML,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildParameter(PentahoReportScheduleConstants.HTML_PROPORTIONAL_WIDTH,
                PentahoReportScheduleConstants.FALSE,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildSingleParameter(PentahoReportScheduleConstants.EMAIL_TO, emailTo,
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildSingleParameter(PentahoReportScheduleConstants.EMAIL_CC, "",
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildSingleParameter(PentahoReportScheduleConstants.EMAIL_BCC, "",
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildSingleParameter(PentahoReportScheduleConstants.EMAIL_SUBJECT,
                String.format(PentahoReportScheduleConstants.EMAIL_SUBJECT_TEMPLATE, jobName),
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildSingleParameter(PentahoReportScheduleConstants.EMAIL_MESSAGE,
                String.format(PentahoReportScheduleConstants.EMAIL_BODY_TEMPLATE, jobName),
                PentahoReportScheduleConstants.STRING));
        jobParameters.add(buildSingleParameter(PentahoReportScheduleConstants.EMAIL_ATTACHMENT, jobName,
                PentahoReportScheduleConstants.STRING));

        scheduleJob.put(PentahoReportScheduleConstants.JOB_PARAMETERS, jobParameters);
    }

    private JSONObject buildParameter(String paramName, String paramValue, String paramType)
    {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(paramValue);
        JSONObject jsonParam = new JSONObject();
        jsonParam.put(PentahoReportScheduleConstants.NAME, paramName);
        jsonParam.put(PentahoReportScheduleConstants.STRING_VALUE, jsonArray);
        jsonParam.put(PentahoReportScheduleConstants.TYPE, paramType);
        return jsonParam;
    }

    private JSONObject buildSingleParameter(String paramName, String paramValue, String paramType)
    {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put(PentahoReportScheduleConstants.NAME, paramName);
        jsonParam.put(PentahoReportScheduleConstants.STRING_VALUE, paramValue);
        jsonParam.put(PentahoReportScheduleConstants.TYPE, paramType);
        return jsonParam;
    }

    public String toJSONSting()
    {
        return scheduleJob.toJSONString();
    }

    public String getTriggerName()
    {
        return triggerName;
    }
}
