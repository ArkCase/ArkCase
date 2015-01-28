/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.form.casefile.model.frevvoxmlmarshal.EmployeeReference;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.frevvoxmlmarshal.GeneralPostalAddress;

/**
 * @author riste.tutureski
 *
 */
public class AddressHistory {
	
	private Long id;
	private Date startDate;
	private Date endDate;
	private PostalAddress location;
	private Subject reference;
	
	
	@XmlElement(name="addressHistoryId")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(name="startDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getStartDate() 
	{
		return startDate;
	}
	
	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}
	
	@XmlElement(name="endDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getEndDate() 
	{
		return endDate;
	}
	
	public void setEndDate(Date endDate) 
	{
		this.endDate = endDate;
	}
	
	@XmlElement(name="location", type=GeneralPostalAddress.class)
	public PostalAddress getLocation() {
		return location;
	}

	public void setLocation(PostalAddress location) {
		this.location = location;
	}

	@XmlElement(name="referenceSection", type=EmployeeReference.class)
	public Subject getReference() 
	{
		return reference;
	}
	
	public void setReference(Subject reference) 
	{
		this.reference = reference;
	}
	
}
