package com.armedia.acm.forms.roi.model;

public class ROIForm {
	String reportInformation;
	String reportTitle;
	String firstName;
	String lastName;
	String preparedDate;
	String complaintNumber;
	String complaintTitle;
	String complaintPriority;
	String reportDetail;
	String reportSummary;
	String internal;
	String acm_ticket;
	String complaint_id;
	
	public ROIForm() {};
	
	public String getReportInformation() {
		return reportInformation;
	}
	public void setReportInformation(String reportInformation) {
		this.reportInformation = reportInformation;
	}

	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPreparedDate() {
		return preparedDate;
	}
	public void setPreparedDate(String preparedDate) {
		this.preparedDate = preparedDate;
	}
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
	public String getReportDetail() {
		return reportDetail;
	}
	public void setReportDetail(String reportDetail) {
		this.reportDetail = reportDetail;
	}
	public String getReportSummary() {
		return reportSummary;
	}
	public void setReportSummary(String reportSummary) {
		this.reportSummary = reportSummary;
	}
	public String getInternal() {
		return internal;
	}
	public void setInternal(String internal) {
		this.internal = internal;
	}
	public String getAcm_ticket() {
		return acm_ticket;
	}
	public void setAcm_ticket(String acm_ticket) {
		this.acm_ticket = acm_ticket;
	}
	public String getComplaint_id() {
		return complaint_id;
	}
	public void setComplaint_id(String complaint_id) {
		this.complaint_id = complaint_id;
	}
	
	@Override
	public String toString() {
		return "ROIForm [reportInformation=" + reportInformation
				+ ", reportTitle=" + reportTitle + ", firstName=" + firstName
				+ ", preparedDate=" + preparedDate + ", complaintNumber="
				+ complaintNumber + ", complaintTitle=" + complaintTitle
				+ ", complaintPriority=" + complaintPriority
				+ ", reportDetail=" + reportDetail + ", reportSummary="
				+ reportSummary + ", internal=" + internal + ", acm_ticket="
				+ acm_ticket + ", complaint_id=" + complaint_id + "]";
	}
	
}
