/**
 * 
 */
package com.armedia.acm.forms.roi.model;

/**
 * @author riste.tutureski
 *
 */
public class ReportDetails {

	private Long complaintId;
	private String complaintNumber;
	private String complaintTitle;
	private String complaintPriority;
	private String summary;
	
	/**
	 * @return the complaintId
	 */
	public Long getComplaintId() {
		return complaintId;
	}
	
	/**
	 * @param complaintId the complaintId to set
	 */
	public void setComplaintId(Long complaintId) {
		this.complaintId = complaintId;
	}
	
	/**
	 * @return the complaintNumber
	 */
	public String getComplaintNumber() {
		return complaintNumber;
	}
	
	/**
	 * @param complaintNumber the complaintNumber to set
	 */
	public void setComplaintNumber(String complaintNumber) {
		this.complaintNumber = complaintNumber;
	}
	
	/**
	 * @return the complaintTitle
	 */
	public String getComplaintTitle() {
		return complaintTitle;
	}
	
	/**
	 * @param complaintTitle the complaintTitle to set
	 */
	public void setComplaintTitle(String complaintTitle) {
		this.complaintTitle = complaintTitle;
	}
	
	/**
	 * @return the complaintPriority
	 */
	public String getComplaintPriority() {
		return complaintPriority;
	}
	
	/**
	 * @param complaintPriority the complaintPriority to set
	 */
	public void setComplaintPriority(String complaintPriority) {
		this.complaintPriority = complaintPriority;
	}
	
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}
	
	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
}
