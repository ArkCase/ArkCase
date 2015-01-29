/**
 * 
 */
package com.armedia.acm.plugins.addressable.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;

/**
 * @author riste.tutureski
 *
 */
public class PeopleContactMethod extends ContactMethod {
	
	private static final long serialVersionUID = -2743783024031491580L;

	public PeopleContactMethod()
	{
		
	}
	
	public PeopleContactMethod(ContactMethod contactMethod)
	{
		setId(contactMethod.getId());
		setCreated(contactMethod.getCreated());
		setCreator(contactMethod.getCreator());
		setType(contactMethod.getType());
		setValue(contactMethod.getValue());
	}
	
	@XmlElement(name="peopleDeviceId")
	@Override
	public Long getId(){
        return super.getId();
    }
	
	@Override
	public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="peopleDeviceDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getCreated() {
		return super.getCreated();
	}
	
	@Override
	public void setCreated(Date created) {
		super.setCreated(created);
	}

	@XmlElement(name="peopleDeviceAddedBy")
	@Override
	public String getCreator() {
		return super.getCreator();
	}
	
	@Override
	public void setCreator(String creator) {
		super.setCreator(creator);
	}

	@XmlElement(name="peopleDeviceType")
	@Override
	public String getType() {
		return super.getType();
	}
	
	@Override
	public void setType(String type) {
		super.setType(type);
	}

	@XmlElement(name="peopleDeviceValue")
	@Override
	public String getValue() {
		return super.getValue();
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
	}
	
	@Override
	public ContactMethod returnBase() {
		ContactMethod base = new ContactMethod();

		base.setId(getId());
		base.setCreated(getCreated());
		base.setCreator(getCreator());
		base.setType(getType());
		base.setValue(getValue());
		
		return base;
	}

}
