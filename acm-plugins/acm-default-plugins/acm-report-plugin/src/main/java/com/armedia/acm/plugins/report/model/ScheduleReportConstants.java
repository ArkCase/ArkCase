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
    public static final String SHOW_PARAMETERS = "true";
    public static final String RENDER_MODE = "XML";
    public static final String HTML_PROPORTIONAL_WIDTH = "false";
    public static final String EXCEL_MIMETYPE = "application/vnd.ms-excel";

    private ScheduleReportConstants()
    {
        throw new AssertionError();
    }
}
