/**
 * 
 */
package com.armedia.acm.plugins.addressable.model.frevvoxmlmarshal;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;

/**
 * @author riste.tutureski
 *
 */
public class InitiatorContactMethod extends ContactMethod {

	private static final long serialVersionUID = 874876446690087952L;
	
	public InitiatorContactMethod()
	{
		
	}
	
	public InitiatorContactMethod(ContactMethod contactMethod)
	{
		setId(contactMethod.getId());
		setCreated(contactMethod.getCreated());
		setCreator(contactMethod.getCreator());
		setType(contactMethod.getType());
		setValue(contactMethod.getValue());
	}
	
	@XmlElement(name="initiatorDeviceId")
	@Override
	public Long getId(){
        return super.getId();
    }
	
	@Override
	public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="initiatorDeviceDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getCreated() {
		return super.getCreated();
	}
	
	@Override
	public void setCreated(Date created) {
		super.setCreated(created);
	}

	@XmlElement(name="initiatorDeviceAddedBy")
	@Override
	public String getCreator() {
		return super.getCreator();
	}
	
	@Override
	public void setCreator(String creator) {
		super.setCreator(creator);
	}

	@XmlElement(name="initiatorDeviceType")
	@Override
	public String getType() {
		return super.getType();
	}
	
	@Override
	public void setType(String type) {
		super.setType(type);
	}

	@XmlElement(name="initiatorDeviceValue")
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
