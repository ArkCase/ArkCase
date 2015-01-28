/**
 * 
 */
package com.armedia.acm.plugins.person.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.person.model.PersonAlias;

/**
 * @author riste.tutureski
 *
 */
public class InitiatorPersonAlias extends PersonAlias {

	private static final long serialVersionUID = 1L;

	public InitiatorPersonAlias()
	{
		
	}
	
	public InitiatorPersonAlias(PersonAlias personAlias)
	{
		setId(personAlias.getId());
		setAliasType(personAlias.getAliasType());
		setAliasValue(personAlias.getAliasValue());
		setCreated(personAlias.getCreated());
		setCreator(personAlias.getCreator());
	}
	
	@XmlElement(name="initiatorAliasId")
	@Override
	public Long getId() {
        return super.getId();
    }
	
	@Override
	public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="initiatorAliasType")
	@Override
	public String getAliasType() {
		return super.getAliasType();
	}
	
	@Override
	public void setAliasType(String aliasType) {
		super.setAliasType(aliasType);
	}

	@XmlElement(name="initiatorAliasValue")
	@Override
	public String getAliasValue() {
		return super.getAliasValue();
	}
	
	@Override
	public void setAliasValue(String aliasValue) {
		super.setAliasValue(aliasValue);
	}

	@XmlElement(name="initiatorAliasDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getCreated() {
		return super.getCreated();
	}
	
	@Override
	public void setCreated(Date created) {
		super.setCreated(created);
	}

	@XmlElement(name="initiatorAliasAddedBy")
	@Override
	public String getCreator() {
		return super.getCreator();
	}
	
	@Override
	public void setCreator(String creator) {
		super.setCreator(creator);
	}
	
	@Override
	public PersonAlias returnBase() {
		PersonAlias base = new PersonAlias();
		
		base.setId(getId());
		base.setAliasType(getAliasType());
		base.setAliasValue(getAliasValue());
		base.setCreated(getCreated());
		base.setCreator(getCreator());
		
		return base;
	}

}
