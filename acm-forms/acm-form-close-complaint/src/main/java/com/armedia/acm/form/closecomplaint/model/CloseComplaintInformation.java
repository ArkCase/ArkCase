/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CloseComplaintInformation {

	private Long complaintId;
	private String complaintNumber;
	private Date closeDate;
	private String disposition;
	private List<String> dispositions;
	
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
	 * @return the closeDate
	 */
	public Date getCloseDate() {
		return closeDate;
	}
	
	/**
	 * @param closeDate the closeDate to set
	 */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}
	
	/**
	 * @return the disposition
	 */
	public String getDisposition() {
		return disposition;
	}
	
	/**
	 * @param disposition the disposition to set
	 */
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	
	/**
	 * @return the dispositions
	 */
	public List<String> getDispositions() {
		return dispositions;
	}
	
	/**
	 * @param dispositions the dispositions to set
	 */
	public void setDispositions(List<String> dispositions) {
		this.dispositions = dispositions;
	}
	
}
