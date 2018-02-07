package com.armedia.acm.plugins.report.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
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
 * "simpleJobTrigger":{"uiPassParam":"RUN_ONCE", "repeatInterval":0, "repeatCount":0, "startTime":"2017-06-06T15:59:00.000-04:00", "endTime":null},
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
public class ScheduleRequest
{
    private JSONObject scheduleJob;

    private JSONObject jobTrigger;
    private String triggerName;

    private JSONArray jobParameters;
    private JSONArray outputTarget;
    private JSONArray acceptedPage;
    private JSONArray showParameters;
    private JSONArray renderMode;
    private JSONArray htmlProportionalWidth;

    private DateTimeFormatter dateTimeFormatter;
    private DateTime dateTime;
    private Calendar calendar;

    public ScheduleRequest(String scheduleType, String startTime, String endTime, String jobName, String emails,
                           String filterStartDate, String filterEndDate, String reportFile, String inputPath, String outputPath)
    {
        dateConversion(startTime);
        scheduleJob = new JSONObject();
        scheduleJob.put(ScheduleReportConstants.JOB_NAME, jobName);

        setTrigger(scheduleType, startTime, endTime);

        scheduleJob.put(ScheduleReportConstants.INPUT_FILE, buildFullPath(inputPath, reportFile));
        scheduleJob.put(ScheduleReportConstants.OUTPUT_FILE, outputPath);

        setJobParameters(emails, filterStartDate, filterEndDate, jobName);
    }

    private String buildFullPath(String base, String path)
    {
        String fullPath = null;
        if (base != null && path != null)
        {
            if (base.endsWith("/") || base.endsWith("\\")) {
                fullPath = base + path;
            } else
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
        jobTrigger.put(ScheduleReportConstants.UI_PASS_PARAM, scheduleType);

        setRepeatInterval(scheduleType);

        jobTrigger.put(ScheduleReportConstants.START_TIME, startTime);
        jobTrigger.put(ScheduleReportConstants.END_TIME, endTime);

        if (scheduleType.equals(ScheduleReportConstants.RUN_ONCE) || scheduleType.equals(ScheduleReportConstants.DAILY))
        {
            triggerName = ScheduleReportConstants.SIMPLE_JOB_TRIGGER;
        } else
        {
            triggerName = ScheduleReportConstants.COMPLEX_JOB_TRIGGER;
        }

        scheduleJob.put(triggerName, jobTrigger);
    }

    private void setRepeatInterval(String scheduleType)
    {
        if (scheduleType.equals(ScheduleReportConstants.DAILY))
        {
            jobTrigger.put(ScheduleReportConstants.REPEAT_INTERVAL, ScheduleReportConstants.DAILY_INTERVAL);
            jobTrigger.put(ScheduleReportConstants.REPEAT_COUNT, "-1");

        } else if (scheduleType.equals(ScheduleReportConstants.WEEKLY))
        {
            //Pentaho dayOfWeek starts from 0 - Sunday, thus - 1.
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek > 0)
            {
                dayOfWeek = dayOfWeek - 1;
            }
            JSONArray dayOfWeekArray = new JSONArray();
            dayOfWeekArray.put(Integer.toString(dayOfWeek));
            jobTrigger.put(ScheduleReportConstants.DAYS_OF_WEEK, dayOfWeekArray);
        } else if (scheduleType.equals(ScheduleReportConstants.MONTHLY))
        {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            JSONArray dayOfMonthArray = new JSONArray();
            dayOfMonthArray.put(Integer.toString(dayOfMonth));
            jobTrigger.put(ScheduleReportConstants.DAYS_OF_MONTH, dayOfMonthArray);
        } else
        {
            //RUN_ONCE
            jobTrigger.put(ScheduleReportConstants.REPEAT_COUNT, 0);
            jobTrigger.put(ScheduleReportConstants.REPEAT_INTERVAL, 0);
        }
    }


    public void setJobParameters(String emailTo, String filterStartDate, String filterEndDate, String jobName)
    {
        jobParameters = new JSONArray();

        //{"name":"output-target", "stringValue":["table/excel;page-mode=flow"],"type":"string"},
        outputTarget = new JSONArray();
        outputTarget.put(ScheduleReportConstants.OUTPUT_TARGET_STRING_VALUE);
        JSONObject target = new JSONObject();
        target.put(ScheduleReportConstants.NAME, ScheduleReportConstants.OUTPUT_TARGET);
        //default outputTarget = "table/excel;page-mode=flow"
        target.put(ScheduleReportConstants.STRING_VALUE, outputTarget);
        target.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(target);

        //{"name":"accepted-page", "stringValue":["-1"], "type":"number"},
        acceptedPage = new JSONArray();
        acceptedPage.put(ScheduleReportConstants.ACCEPTED_PAGE_STRING_VALUE);
        JSONObject accepted = new JSONObject();
        accepted.put(ScheduleReportConstants.NAME, ScheduleReportConstants.ACCEPTED_PAGE);
        accepted.put(ScheduleReportConstants.STRING_VALUE, acceptedPage);
        accepted.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.NUMBER);
        jobParameters.put(accepted);

        JSONArray startDateArray = new JSONArray();
        startDateArray.put(filterStartDate);
        JSONObject startDate = new JSONObject();
        startDate.put(ScheduleReportConstants.NAME, ScheduleReportConstants.START_DATE);
        startDate.put(ScheduleReportConstants.STRING_VALUE, startDateArray);
        startDate.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(startDate);

        JSONArray endDateArray = new JSONArray();
        endDateArray.put(filterEndDate);
        JSONObject endDate = new JSONObject();
        endDate.put(ScheduleReportConstants.NAME, ScheduleReportConstants.END_DATE);
        endDate.put(ScheduleReportConstants.STRING_VALUE, endDateArray);
        endDate.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(endDate);

        JSONArray dateFormatArray = new JSONArray();
        dateFormatArray.put(ScheduleReportConstants.REPORT_DATE_FORMAT);
        JSONObject dateFormat = new JSONObject();
        dateFormat.put(ScheduleReportConstants.NAME, ScheduleReportConstants.DATE_FORMAT);
        dateFormat.put(ScheduleReportConstants.STRING_VALUE, dateFormatArray);
        dateFormat.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(dateFormat);

        JSONArray timeZoneArray = new JSONArray();
        timeZoneArray.put(ScheduleReportConstants.UTC);
        JSONObject timeZone = new JSONObject();
        timeZone.put(ScheduleReportConstants.NAME, ScheduleReportConstants.TIME_ZONE);
        timeZone.put(ScheduleReportConstants.STRING_VALUE, timeZoneArray);
        timeZone.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(timeZone);

        JSONArray statusArray = new JSONArray();
        statusArray.put(ScheduleReportConstants.WILDCARD);
        JSONObject status = new JSONObject();
        status.put(ScheduleReportConstants.NAME, ScheduleReportConstants.STATUS);
        status.put(ScheduleReportConstants.STRING_VALUE, statusArray);
        status.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(status);

        //{"name":"showParameters", "stringValue":["true"], "type":"string"},
        showParameters = new JSONArray();
        showParameters.put(ScheduleReportConstants.TRUE);
        JSONObject show = new JSONObject();
        show.put(ScheduleReportConstants.NAME, ScheduleReportConstants.SHOW_PARAMETERS);
        show.put(ScheduleReportConstants.STRING_VALUE, showParameters);
        show.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(show);

        //{"name":"renderMode", "stringValue":["XML"], "type":"string"},
        renderMode = new JSONArray();
        renderMode.put(ScheduleReportConstants.XML);
        JSONObject render = new JSONObject();
        render.put(ScheduleReportConstants.NAME, ScheduleReportConstants.RENDER_MODE);
        render.put(ScheduleReportConstants.STRING_VALUE, renderMode);
        render.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(render);

        //{"name":"htmlProportionalWidth", "stringValue":["false"], "type":"string"},
        htmlProportionalWidth = new JSONArray();
        htmlProportionalWidth.put(ScheduleReportConstants.FALSE);
        JSONObject htlmWidth = new JSONObject();
        htlmWidth.put(ScheduleReportConstants.NAME, ScheduleReportConstants.HTML_PROPORTIONAL_WIDTH);
        htlmWidth.put(ScheduleReportConstants.STRING_VALUE, htmlProportionalWidth);
        htlmWidth.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(htlmWidth);

        //{"name":"_SCH_EMAIL_TO", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
        JSONObject emailsTo = new JSONObject();
        emailsTo.put(ScheduleReportConstants.NAME, ScheduleReportConstants.EMAIL_TO);
        emailsTo.put(ScheduleReportConstants.STRING_VALUE, emailTo);
        emailsTo.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(emailsTo);

        //{"name":"_SCH_EMAIL_CC", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
        JSONObject emailsCC = new JSONObject();
        emailsCC.put(ScheduleReportConstants.NAME, ScheduleReportConstants.EMAIL_CC);
        emailsCC.put(ScheduleReportConstants.STRING_VALUE, "");
        emailsCC.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(emailsCC);

        //{"name":"_SCH_EMAIL_BCC", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
        JSONObject emailsBCC = new JSONObject();
        emailsBCC.put(ScheduleReportConstants.NAME, ScheduleReportConstants.EMAIL_BCC);
        emailsBCC.put(ScheduleReportConstants.STRING_VALUE, "");
        emailsBCC.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(emailsBCC);

        //{"name":"_SCH_EMAIL_SUBJECT", "stringValue":"MasterList schedule has successfully run.", "type":"string"},
        JSONObject subject = new JSONObject();
        subject.put(ScheduleReportConstants.NAME, ScheduleReportConstants.EMAIL_SUBJECT);
        subject.put(ScheduleReportConstants.STRING_VALUE, String.format(ScheduleReportConstants.EMAIL_SUBJECT_TEMPLATE, jobName));
        subject.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(subject);

        //{"name":"_SCH_EMAIL_MESSAGE", "stringValue":"this is a scheduled job", "type":"string"},
        JSONObject msg = new JSONObject();
        msg.put(ScheduleReportConstants.NAME, ScheduleReportConstants.EMAIL_MESSAGE);
        msg.put(ScheduleReportConstants.STRING_VALUE, String.format(ScheduleReportConstants.EMAIL_BODY_TEMPLATE, jobName));
        msg.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(msg);

        //{"name":"_SCH_EMAIL_ATTACHMENT_NAME", "stringValue":"MasterList", "type":"string"}]
        JSONObject attachment = new JSONObject();
        attachment.put(ScheduleReportConstants.NAME, ScheduleReportConstants.EMAIL_ATTACHMENT);
        attachment.put(ScheduleReportConstants.STRING_VALUE, jobName);
        attachment.put(ScheduleReportConstants.TYPE, ScheduleReportConstants.STRING);
        jobParameters.put(attachment);

        scheduleJob.put(ScheduleReportConstants.JOB_PARAMETERS, jobParameters);
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
