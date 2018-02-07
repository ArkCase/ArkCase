package com.armedia.acm.plugins.report.model;

/**
 * Created by dwu on 6/9/2017.
 * <p>
 * "jobName":"MasterList",
 * "simpleJobTrigger":{"uiPassParam":"RUN_ONCE", "repeatInterval":0, "repeatCount":0, "startTime":"2017-06-06T15:59:00.000-04:00", "endTime":null},
 * "inputFile":"/public/arkcase/MasterList.prpt",
 * "outputFile":"/public/admin",
 * "jobParameters":[
 * {"name":"output-target", "stringValue":["table/excel;page-mode=flow"],"type":"string"},
 * {"name":"accepted-page", "stringValue":["-1"], "type":"number"},
 * {"name":"showParameters", "stringValue":["true"], "type":"string"},
 * {"name":"renderMode", "stringValue":["XML"], "type":"string"},
 * {"name":"htmlProportionalWidth", "stringValue":["false"], "type":"string"},
 * {"name":"_SCH_EMAIL_TO", "stringValue":"david.wu@armedia.com;wudc@yahoo.com", "type":"string"},
 * {"name":"_SCH_EMAIL_SUBJECT", "stringValue":"MasterList schedule has successfully run.", "type":"string"},
 * {"name":"_SCH_EMAIL_MESSAGE", "stringValue":"this is a scheduled job", "type":"string"},
 * {"name":"_SCH_EMAIL_ATTACHMENT_NAME", "stringValue":"MasterList", "type":"string"}]
 * }
 */

public final class ScheduleReportConstants
{
    //These are default values
    public static final String SIMPLE_JOB_TRIGGER = "simpleJobTrigger";
    public static final String COMPLEX_JOB_TRIGGER = "complexJobTrigger";
    public static final String RUN_ONCE = "RUN_ONCE";
    public static final String DAILY = "DAILY";
    public static final String DAILY_INTERVAL = "86400";
    public static final String WEEKLY = "WEEKLY";
    public static final String MONTHLY = "MONTHLY";
    public static final String OUTPUT_TARGET_STRING_VALUE = "table/excel;page-mode=flow";
    public static final String ACCEPTED_PAGE_STRING_VALUE = "-1";
    public static final String TRUE = "true";
    public static final String XML = "XML";
    public static final String FALSE = "false";
    public static final String EXCEL_MIMETYPE = "application/vnd.ms-excel";

    public static final String JOB_NAME = "jobName";
    public static final String INPUT_FILE = "inputFile";
    public static final String OUTPUT_FILE = "outputFile";
    public static final String UI_PASS_PARAM = "uiPassParam";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String REPEAT_INTERVAL = "repeatInterval";
    public static final String REPEAT_COUNT = "repeatCount";
    public static final String DAYS_OF_WEEK = "daysOfWeek";
    public static final String DAYS_OF_MONTH = "daysOfMonth";
    public static final String NAME = "name";
    public static final String STRING_VALUE = "stringValue";
    public static final String TYPE = "type";
    public static final String STRING = "string";
    public static final String NUMBER = "number";
    public static final String OUTPUT_TARGET = "output-target";
    public static final String ACCEPTED_PAGE = "accepted-page";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String REPORT_DATE_FORMAT = "MM/DD/YYYY";
    public static final String DATE_FORMAT = "dateFormat";
    public static final String UTC = "UTC5:0";
    public static final String TIME_ZONE = "timeZone";
    public static final String WILDCARD = "%";
    public static final String STATUS = "status";
    public static final String SHOW_PARAMETERS = "showParameters";
    public static final String RENDER_MODE = "renderMode";
    public static final String HTML_PROPORTIONAL_WIDTH = "htmlProportionalWidth";
    public static final String EMAIL_TO = "_SCH_EMAIL_TO";
    public static final String EMAIL_CC = "_SCH_EMAIL_CC";
    public static final String EMAIL_BCC = "_SCH_EMAIL_BCC";
    public static final String EMAIL_SUBJECT = "_SCH_EMAIL_SUBJECT";
    public static final String EMAIL_MESSAGE = "_SCH_EMAIL_MESSAGE";
    public static final String EMAIL_ATTACHMENT = "_SCH_EMAIL_ATTACHMENT_NAME";
    public static final String EMAIL_SUBJECT_TEMPLATE = "%s scheduled report has successfully run";
    public static final String EMAIL_BODY_TEMPLATE = "Attached is the scheduled report \"%s\"";
    public static final String JOB_PARAMETERS = "jobParameters";

    private ScheduleReportConstants()
    {
        throw new AssertionError();
    }
}
