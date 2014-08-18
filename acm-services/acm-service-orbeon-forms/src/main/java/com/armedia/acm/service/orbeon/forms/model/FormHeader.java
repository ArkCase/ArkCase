package com.armedia.acm.service.orbeon.forms.model;

public class FormHeader {
	String reportInformation;
	String reportTitle;
	String firstName;
	String lastName;
	String preparedDate;
	
	public FormHeader() {}

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

	@Override
	public String toString() {
		return "FormHeader [reportInformation=" + reportInformation
				+ ", reportTitle=" + reportTitle + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", preparedDate=" + preparedDate
				+ "]";
	}

}
