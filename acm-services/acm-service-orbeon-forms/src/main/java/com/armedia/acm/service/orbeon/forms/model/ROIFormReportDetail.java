package com.armedia.acm.service.orbeon.forms.model;

public class ROIFormReportDetail {
	String complaintNumber;
	String complaintTitle;
	String complaintPriority;

	String reportSummary;
	
	public ROIFormReportDetail() {}

	public String getComplaintNumber() {
		return complaintNumber;
	}

	public void setComplaintNumber(String complaintNumber) {
		this.complaintNumber = complaintNumber;
	}

	public String getComplaintTitle() {
		return complaintTitle;
	}

	public void setComplaintTitle(String complaintTitle) {
		this.complaintTitle = complaintTitle;
	}

	public String getComplaintPriority() {
		return complaintPriority;
	}

	public void setComplaintPriority(String complaintPriority) {
		this.complaintPriority = complaintPriority;
	}

	public String getReportSummary() {
		return reportSummary;
	}

	public void setReportSummary(String reportSummary) {
		this.reportSummary = reportSummary;
	}

	@Override
	public String toString() {
		return "ROIFormReportDetail [complaintNumber=" + complaintNumber
				+ ", complaintTitle=" + complaintTitle + ", complaintPriority="
				+ complaintPriority + ", reportSummary=" + reportSummary + "]";
	}
	
}
