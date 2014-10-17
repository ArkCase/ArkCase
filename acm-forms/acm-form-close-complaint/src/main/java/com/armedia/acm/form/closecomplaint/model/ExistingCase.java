/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ExistingCase {
	
	private String caseNumber;
	private String caseTitle;
	private Date caseCreationDate;
	private String casePriority;
	
	/**
	 * @return the caseNumber
	 */
	public String getCaseNumber() {
		return caseNumber;
	}
	
	/**
	 * @param caseNumber the caseNumber to set
	 */
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	
	/**
	 * @return the caseTitle
	 */
	public String getCaseTitle() {
		return caseTitle;
	}
	
	/**
	 * @param caseTitle the caseTitle to set
	 */
	public void setCaseTitle(String caseTitle) {
		this.caseTitle = caseTitle;
	}
	
	/**
	 * @return the caseCreationDate
	 */
	public Date getCaseCreationDate() {
		return caseCreationDate;
	}
	
	/**
	 * @param caseCreationDate the caseCreationDate to set
	 */
	public void setCaseCreationDate(Date caseCreationDate) {
		this.caseCreationDate = caseCreationDate;
	}
	
	/**
	 * @return the casePriority
	 */
	public String getCasePriority() {
		return casePriority;
	}
	
	/**
	 * @param casePriority the casePriority to set
	 */
	public void setCasePriority(String casePriority) {
		this.casePriority = casePriority;
	}
}
