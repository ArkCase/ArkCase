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
public class WitnessVictimPostalAddress extends PostalAddress{

	private static final long serialVersionUID = -656234510581648369L;

	public WitnessVictimPostalAddress()
	{
		
	}
	
	public WitnessVictimPostalAddress(PostalAddress postalAddress)
	{
		setId(postalAddress.getId());
		setType(postalAddress.getType());
		setStreetAddress(postalAddress.getStreetAddress());
		setCity(postalAddress.getCity());
		setState(postalAddress.getState());
		setZip(postalAddress.getZip());
	}
	
	@XmlElement(name="witnessVictimLocationId")
    public Long getId() {
        return super.getId();
    }

	@XmlElement(name="witnessVictimLocationType")
	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	public void setType(String type) {
		super.setType(type);
	}
	
	@XmlElement(name="witnessVictimLocationAddress")
	@Override
	public String getStreetAddress() {
		return super.getStreetAddress();
	}
	
	@Override
	public void setStreetAddress(String streetAddress) {
		super.setStreetAddress(streetAddress);
	}

	@XmlElement(name="witnessVictimLocationCity")
	@Override
	public String getCity() {
		return super.getCity();
	}
	
	@Override
	public void setCity(String city) {
		super.setCity(city);
	}

	@XmlElement(name="witnessVictimLocationState")
	@Override
	public String getState() {
		return super.getState();
	}

	@Override
	public void setState(String state) {
		super.setState(state);
	}
	
	@XmlElement(name="witnessVictimLocationZip")
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
