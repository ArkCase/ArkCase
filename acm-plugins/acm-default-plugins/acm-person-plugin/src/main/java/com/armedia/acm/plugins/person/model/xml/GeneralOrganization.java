/**
 * 
 */
package com.armedia.acm.plugins.person.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.person.model.Organization;

/**
 * @author riste.tutureski
 *
 */
public class GeneralOrganization extends Organization {

	private static final long serialVersionUID = 1L;
	
	public GeneralOrganization()
	{
		
	}
	
	public GeneralOrganization(Organization organization)
	{
		setOrganizationId(organization.getOrganizationId());
		setOrganizationType(organization.getOrganizationType());
		setOrganizationValue(organization.getOrganizationValue());
		setCreated(organization.getCreated());
		setCreator(organization.getCreator());
	}
	
	@XmlElement(name="organizationId")
	@Override
	public Long getOrganizationId(){
        return super.getOrganizationId();
    }
	
	@Override
	public void setOrganizationId(Long organizationId){
        super.setOrganizationId(organizationId);
    }

	@XmlElement(name="organizationType")
	@Override
	public String getOrganizationType() {
		return super.getOrganizationType();
	}

	@Override
	public void setOrganizationType(String organizationType) {
		super.setOrganizationType(organizationType);
	}
	
	@XmlElement(name="organizationValue")
	@Override
	public String getOrganizationValue() {
		return super.getOrganizationValue();
	}

	@Override
	public void setOrganizationValue(String organizationValue) {
		super.setOrganizationValue(organizationValue);
	}
	
	@XmlElement(name="created")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getCreated() {
		return super.getCreated();
	}
	
	@Override
	public void setCreated(Date created) {
		super.setCreated(created);
	}

	@XmlElement(name="creator")
	@Override
	public String getCreator() {
		return super.getCreator();
	}
	
	@Override
	public void setCreator(String creator) {
		super.setCreator(creator);
	}
	
	@Override
	public Organization returnBase() {
		Organization base = new Organization();
		
		base.setOrganizationId(getOrganizationId());
		base.setOrganizationType(getOrganizationType());
		base.setOrganizationValue(getOrganizationValue());
		base.setCreated(getCreated());
		base.setCreator(getCreator());
		
		return base;
	}

}
