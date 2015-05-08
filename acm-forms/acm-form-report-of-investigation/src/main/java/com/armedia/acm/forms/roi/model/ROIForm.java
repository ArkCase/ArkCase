package com.armedia.acm.forms.roi.model;

import com.armedia.acm.form.config.Item;
import com.armedia.acm.form.config.xml.ApproverItem;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class ROIForm {
	
	private ReportInformation reportInformation;
	private ReportDetails reportDetails;
	private List<Item> approvers;

	/**
	 * @return the reportInformation
	 */
	public ReportInformation getReportInformation() {
		return reportInformation;
	}
	
	/**
	 * @param reportInformation the reportInformation to set
	 */
	public void setReportInformation(ReportInformation reportInformation) {
		this.reportInformation = reportInformation;
	}
	
	/**
	 * @return the reportDetails
	 */
	public ReportDetails getReportDetails() {
		return reportDetails;
	}
	
	/**
	 * @param reportDetails the reportDetails to set
	 */
	public void setReportDetails(ReportDetails reportDetails) {
		this.reportDetails = reportDetails;
	}

	@XmlElement(name="approverItem", type=ApproverItem.class)
	public List<Item> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<Item> approvers)
	{
		this.approvers = approvers;
	}
}
