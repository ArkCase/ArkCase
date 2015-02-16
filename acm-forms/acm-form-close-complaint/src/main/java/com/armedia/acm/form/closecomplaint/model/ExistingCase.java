/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class ExistingCase {
	
	private String caseNumber;
	private String caseTitle;
	private Date caseCreationDate;
	private String casePriority;
	
	public String getCaseNumber() {
		return caseNumber;
	}
	
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	
	public String getCaseTitle() {
		return caseTitle;
	}

	public void setCaseTitle(String caseTitle) {
		this.caseTitle = caseTitle;
	}
	
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getCaseCreationDate() {
		return caseCreationDate;
	}
	
	public void setCaseCreationDate(Date caseCreationDate) {
		this.caseCreationDate = caseCreationDate;
	}
	
	public String getCasePriority() {
		return casePriority;
	}
	
	public void setCasePriority(String casePriority) {
		this.casePriority = casePriority;
	}
}
