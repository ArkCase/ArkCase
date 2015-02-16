/**
 * 
 */
package com.armedia.acm.plugins.addressable.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;

/**
 * @author riste.tutureski
 *
 */
public class GeneralPostalAddress extends PostalAddress{

	private static final long serialVersionUID = 1063261705736285472L;

	public GeneralPostalAddress()
	{
		
	}
	
	public GeneralPostalAddress(PostalAddress postalAddress)
	{
		setId(postalAddress.getId());
		setCreated(postalAddress.getCreated());
		setCreator(postalAddress.getCreator());
		setType(postalAddress.getType());
		setStreetAddress(postalAddress.getStreetAddress());
		setCity(postalAddress.getCity());
		setState(postalAddress.getState());
		setZip(postalAddress.getZip());
	}
	
	@XmlElement(name="locationId")
	@Override
    public Long getId() {
        return super.getId();
    }
	
	@Override
    public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="locationDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getCreated() {
		return super.getCreated();
	}

	@Override
	public void setCreated(Date created) {
		super.setCreated(created);
	}
	
	@XmlElement(name="locationAddedBy")
	@Override
	public String getCreator() {
		return super.getCreator();
	}

	@Override
	public void setCreator(String creator) {
		super.setCreator(creator);
	}
	
	@XmlElement(name="locationType")
	@Override
	public String getType() {
		return super.getType();
	}
	
	@Override
	public void setType(String type) {
		super.setType(type);
	}

	@XmlElement(name="locationAddress")
	@Override
	public String getStreetAddress() {
		return super.getStreetAddress();
	}

	@Override
	public void setStreetAddress(String streetAddress) {
		super.setStreetAddress(streetAddress);
	}
	
	@XmlElement(name="locationCity")
	@Override
	public String getCity() {
		return super.getCity();
	}
	
	@Override
	public void setCity(String city) {
		super.setCity(city);
	}

	@XmlElement(name="locationState")
	@Override
	public String getState() {
		return super.getState();
	}

	@Override
	public void setState(String state) {
		super.setState(state);
	}
	
	@XmlElement(name="locationZip")
	@Override
	public String getZip() {
		return super.getZip();
	}
	
	@Override
	public void setZip(String zip) {
		super.setZip(zip);
	}
	
	@Override
	public PostalAddress returnBase() {
		PostalAddress base = new PostalAddress();
		
		base.setId(getId());
		base.setCreated(getCreated());
		base.setCreator(getCreator());
		base.setType(getType());
		base.setStreetAddress(getStreetAddress());
		base.setCity(getCity());
		base.setState(getState());
		base.setZip(getZip());
		
		return base;
	}
}
