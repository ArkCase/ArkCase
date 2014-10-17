/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import java.util.Date;

import com.armedia.acm.plugins.addressable.model.ContactMethod;

/**
 * @author riste.tutureski
 *
 */
public class ReferExternal {

	private String agency;
	private Date date;
	private String person;
	private ContactMethod contact;
	
	/**
	 * @return the agency
	 */
	public String getAgency() {
		return agency;
	}
	
	/**
	 * @param agency the agency to set
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the person
	 */
	public String getPerson() {
		return person;
	}
	
	/**
	 * @param person the person to set
	 */
	public void setPerson(String person) {
		this.person = person;
	}

	/**
	 * @return the contact
	 */
	public ContactMethod getContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public void setContact(ContactMethod contact) {
		this.contact = contact;
	}	
	
}
