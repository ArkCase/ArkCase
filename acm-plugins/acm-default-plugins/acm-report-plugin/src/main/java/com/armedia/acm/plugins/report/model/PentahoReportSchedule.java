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

/**
 * Created by dwu on 6/13/2017.
 */
public class PentahoReportSchedule
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
        return "PentahoReportSchedule{" +
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
