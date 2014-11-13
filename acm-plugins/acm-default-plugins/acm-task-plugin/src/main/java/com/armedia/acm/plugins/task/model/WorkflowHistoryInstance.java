/**
 * 
 */
package com.armedia.acm.plugins.task.model;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class WorkflowHistoryInstance {

	private String id;
	private String participant;
	private String status;
	private Date startDate;
	private Date endDate;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the participant
	 */
	public String getParticipant() {
		return participant;
	}
	
	/**
	 * @param participant the participant to set
	 */
	public void setParticipant(String participant) {
		this.participant = participant;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
