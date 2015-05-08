/**
 * 
 */
package com.armedia.acm.plugins.addressable.model.xml;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.plugins.addressable.model.PostalAddress;

/**
 * @author riste.tutureski
 *
 */
public class OfficerPostalAddress extends PostalAddress{

	private static final long serialVersionUID = -656234510581648369L;

	public OfficerPostalAddress()
	{
		
	}
	
	public OfficerPostalAddress(PostalAddress postalAddress)
	{
		setId(postalAddress.getId());
		setType(postalAddress.getType());
		setStreetAddress(postalAddress.getStreetAddress());
		setCity(postalAddress.getCity());
		setState(postalAddress.getState());
		setZip(postalAddress.getZip());
	}
	
	@XmlElement(name="officerLocationId")
    public Long getId() {
        return super.getId();
    }

	@XmlElement(name="officerLocationType")
	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	public void setType(String type) {
		super.setType(type);
	}
	
	@XmlElement(name="officerLocationAddress")
	@Override
	public String getStreetAddress() {
		return super.getStreetAddress();
	}
	
	@Override
	public void setStreetAddress(String streetAddress) {
		super.setStreetAddress(streetAddress);
	}

	@XmlElement(name="officerLocationCity")
	@Override
	public String getCity() {
		return super.getCity();
	}
	
	@Override
	public void setCity(String city) {
		super.setCity(city);
	}

	@XmlElement(name="officerLocationState")
	@Override
	public String getState() {
		return super.getState();
	}

	@Override
	public void setState(String state) {
		super.setState(state);
	}
	
	@XmlElement(name="officerLocationZip")
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
		base.setType(getType());
		base.setStreetAddress(getStreetAddress());
		base.setCity(getCity());
		base.setState(getState());
		base.setZip(getZip());
		
		return base;
	}

}
