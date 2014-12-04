/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.armedia.acm.plugins.addressable.model.PostalAddress;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressHistory {

	private Date startDate;
	private Date endDate;
	private PostalAddress location;
	
	@XmlElements({
		@XmlElement(name="reference"),
		@XmlElement(name="referenceSection")
	})
	private Subject reference;
	
	public Date getStartDate() 
	{
		return startDate;
	}
	
	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}
	
	public Date getEndDate() 
	{
		return endDate;
	}
	
	public void setEndDate(Date endDate) 
	{
		this.endDate = endDate;
	}
	
	public PostalAddress getLocation() {
		return location;
	}

	public void setLocation(PostalAddress location) {
		this.location = location;
	}

	public Subject getReference() 
	{
		return reference;
	}
	
	public void setReference(Subject reference) 
	{
		this.reference = reference;
	}
	
}
