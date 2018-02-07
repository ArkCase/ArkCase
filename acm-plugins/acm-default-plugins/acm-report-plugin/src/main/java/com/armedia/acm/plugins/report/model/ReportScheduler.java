package com.armedia.acm.plugins.report.model;

/**
 * Created by dwu on 6/13/2017.
 */
public class ReportScheduler
{
    private String uiPassParam;
    private String startTime;
    private String endTime;
    private String jobName;
    private String outputFileType;
    private String emails;
    private String reportFile;
    private String filterStartDate;
    private String filterEndDate;

    public String getUiPassParam()
    {
        return uiPassParam;
    }

    public void setUiPassParam(String uiPassParam)
    {
        this.uiPassParam = uiPassParam;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getOutputFileType()
    {
        return outputFileType;
    }

    public void setOutputFileType(String outputFileType)
    {
        this.outputFileType = outputFileType;
    }

    public String getEmails()
    {
        return emails;
    }

    public void setEmails(String emails)
    {
        this.emails = emails;
    }

    public String getReportFile()
    {
        return reportFile;
    }

    public void setReportFile(String reportFile)
    {
        this.reportFile = reportFile;
    }

    public String getFilterStartDate()
    {
        return filterStartDate;
    }

    public void setFilterStartDate(String filterStartDate)
    {
        this.filterStartDate = filterStartDate;
    }

    public String getFilterEndDate()
    {
        return filterEndDate;
    }

    public void setFilterEndDate(String filterEndDate)
    {
        this.filterEndDate = filterEndDate;
    }

    @Override
    public String toString()
    {
        return "ReportScheduler{" +
                "uiPassParam='" + uiPassParam + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", jobName='" + jobName + '\'' +
                ", outputFileType='" + outputFileType + '\'' +
                ", emails='" + emails + '\'' +
                ", reportFile='" + reportFile + '\'' +
                ", filterStartDate='" + filterStartDate + '\'' +
                ", filterEndDate='" + filterEndDate + '\'' +
                '}';
    }
}
