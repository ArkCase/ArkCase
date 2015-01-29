/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.xml.GeneralContactMethod;

/**
 * @author riste.tutureski
 *
 */
public class ReferExternal {

	private String agency;
	private Date date;
	private String person;
	
	
	private ContactMethod contact;
	
	public String getAgency() {
		return agency;
	}
	
	public void setAgency(String agency) {
		this.agency = agency;
	}
	
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPerson() {
		return person;
	}
	
	public void setPerson(String person) {
		this.person = person;
	}

	@XmlElement(name="contact", type=GeneralContactMethod.class)
	public ContactMethod getContact() {
		return contact;
	}

	public void setContact(ContactMethod contact) {
		this.contact = contact;
	}	
	
}
