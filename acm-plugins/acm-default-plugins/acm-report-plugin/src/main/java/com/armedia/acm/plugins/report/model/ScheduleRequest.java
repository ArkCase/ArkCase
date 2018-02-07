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
        scheduleJob.put("jobName", jobName);

        setTrigger(scheduleType, startTime, endTime);

        scheduleJob.put("inputFile", buildFullPath(inputPath, reportFile));
        scheduleJob.put("outputFile", outputPath);

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
        jobTrigger.put("uiPassParam", scheduleType);

        setRepeatInterval(scheduleType);

        jobTrigger.put("startTime", startTime);
        jobTrigger.put("endTime", endTime);

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
            jobTrigger.put("repeatInterval", ScheduleReportConstants.DAILY_INTERVAL);
            jobTrigger.put("repeatCount", "-1");

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
            jobTrigger.put("daysOfWeek", dayOfWeekArray);
        } else if (scheduleType.equals(ScheduleReportConstants.MONTHLY))
        {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            JSONArray dayOfMonthArray = new JSONArray();
            dayOfMonthArray.put(Integer.toString(dayOfMonth));
            jobTrigger.put("daysOfMonth", dayOfMonthArray);
        } else
        {
            //RUN_ONCE
            jobTrigger.put("repeatCount", 0);
            jobTrigger.put("repeatInterval", 0);
        }
    }


    public void setJobParameters(String emailTo, String filterStartDate, String filterEndDate, String jobName)
    {
        jobParameters = new JSONArray();

        //{"name":"output-target", "stringValue":["table/excel;page-mode=flow"],"type":"string"},
        outputTarget = new JSONArray();
        outputTarget.put(ScheduleReportConstants.OUTPUT_TARGET_STRING_VALUE);
        JSONObject target = new JSONObject();
        target.put("name", "output-target");
        //default outputTarget = "table/excel;page-mode=flow"
        target.put("stringValue", outputTarget);
        target.put("type", "string");
        jobParameters.put(target);

        //{"name":"accepted-page", "stringValue":["-1"], "type":"number"},
        acceptedPage = new JSONArray();
        acceptedPage.put(ScheduleReportConstants.ACCEPTED_PAGE_STRING_VALUE);
        JSONObject accepted = new JSONObject();
        accepted.put("name", "accepted-page");
        accepted.put("stringValue", acceptedPage);
        accepted.put("type", "number");
        jobParameters.put(accepted);

        JSONArray startDateArray = new JSONArray();
        startDateArray.put(filterStartDate);
        JSONObject startDate = new JSONObject();
        startDate.put("name", "startDate");
        startDate.put("stringValue", startDateArray);
        startDate.put("type", "string");
        jobParameters.put(startDate);

        JSONArray endDateArray = new JSONArray();
        endDateArray.put(filterEndDate);
        JSONObject endDate = new JSONObject();
        endDate.put("name", "endDate");
        endDate.put("stringValue", endDateArray);
        endDate.put("type", "string");
        jobParameters.put(endDate);

        JSONArray dateFormatArray = new JSONArray();
        dateFormatArray.put("MM/DD/YYYY");
        JSONObject dateFormat = new JSONObject();
        dateFormat.put("name", "dateFormat");
        dateFormat.put("stringValue", dateFormatArray);
        dateFormat.put("type", "string");
        jobParameters.put(dateFormat);

        JSONArray timeZoneArray = new JSONArray();
        timeZoneArray.put("UTC5:0");
        JSONObject timeZone = new JSONObject();
        timeZone.put("name", "timeZone");
        timeZone.put("stringValue", timeZoneArray);
        timeZone.put("type", "string");
        jobParameters.put(timeZone);

        JSONArray statusArray = new JSONArray();
        statusArray.put("%");
        JSONObject status = new JSONObject();
        status.put("name", "status");
        status.put("stringValue", statusArray);
        status.put("type", "string");
        jobParameters.put(status);

        //{"name":"showParameters", "stringValue":["true"], "type":"string"},
        showParameters = new JSONArray();
        showParameters.put(ScheduleReportConstants.SHOW_PARAMETERS);
        JSONObject show = new JSONObject();
        show.put("name", "showParameters");
        show.put("stringValue", showParameters);
        show.put("type", "string");
        jobParameters.put(show);

        //{"name":"renderMode", "stringValue":["XML"], "type":"string"},
        renderMode = new JSONArray();
        renderMode.put(ScheduleReportConstants.RENDER_MODE);
        JSONObject render = new JSONObject();
        render.put("name", "renderMode");
        render.put("stringValue", renderMode);
        render.put("type", "string");
        jobParameters.put(render);

        //{"name":"htmlProportionalWidth", "stringValue":["false"], "type":"string"},
        htmlProportionalWidth = new JSONArray();
        htmlProportionalWidth.put(ScheduleReportConstants.HTML_PROPORTIONAL_WIDTH);
        JSONObject htlmWidth = new JSONObject();
        htlmWidth.put("name", "htmlProportionalWidth");
        htlmWidth.put("stringValue", htmlProportionalWidth);
        htlmWidth.put("type", "string");
        jobParameters.put(htlmWidth);

        //{"name":"_SCH_EMAIL_TO", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
        JSONObject emailsTo = new JSONObject();
        emailsTo.put("name", "_SCH_EMAIL_TO");
        emailsTo.put("stringValue", emailTo);
        emailsTo.put("type", "string");
        jobParameters.put(emailsTo);

        //{"name":"_SCH_EMAIL_CC", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
        JSONObject emailsCC = new JSONObject();
        emailsCC.put("name", "_SCH_EMAIL_CC");
        emailsCC.put("stringValue", "");
        emailsCC.put("type", "string");
        jobParameters.put(emailsCC);

        //{"name":"_SCH_EMAIL_BCC", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
        JSONObject emailsBCC = new JSONObject();
        emailsBCC.put("name", "_SCH_EMAIL_BCC");
        emailsBCC.put("stringValue", "");
        emailsBCC.put("type", "string");
        jobParameters.put(emailsBCC);

        //{"name":"_SCH_EMAIL_SUBJECT", "stringValue":"MasterList schedule has successfully run.", "type":"string"},
        JSONObject subject = new JSONObject();
        subject.put("name", "_SCH_EMAIL_SUBJECT");
        subject.put("stringValue", jobName + " scheduled report has successfully run");
        subject.put("type", "string");
        jobParameters.put(subject);

        //{"name":"_SCH_EMAIL_MESSAGE", "stringValue":"this is a scheduled job", "type":"string"},
        JSONObject msg = new JSONObject();
        msg.put("name", "_SCH_EMAIL_MESSAGE");
        msg.put("stringValue", "Attached is the scheduled report \"" + jobName + "\"");
        msg.put("type", "string");
        jobParameters.put(msg);

        //{"name":"_SCH_EMAIL_ATTACHMENT_NAME", "stringValue":"MasterList", "type":"string"}]
        JSONObject attachment = new JSONObject();
        attachment.put("name", "_SCH_EMAIL_ATTACHMENT_NAME");
        attachment.put("stringValue", jobName);
        attachment.put("type", "string");
        jobParameters.put(attachment);

        scheduleJob.put("jobParameters", jobParameters);
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
