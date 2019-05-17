package com.armedia.acm.plugins.report.model;

/**
 * @author aleksandar.bujaroski
 */
public class ReportParameters
{
    private String reportName;
    private String dataSearchType;

    public ReportParameters(String reportName, String dataSearchType) {
        this.reportName = reportName;
        this.dataSearchType = dataSearchType;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getDataSearchType() {
        return dataSearchType;
    }

    public void setDataSearchType(String dataSearchType) {
        this.dataSearchType = dataSearchType;
    }
}
