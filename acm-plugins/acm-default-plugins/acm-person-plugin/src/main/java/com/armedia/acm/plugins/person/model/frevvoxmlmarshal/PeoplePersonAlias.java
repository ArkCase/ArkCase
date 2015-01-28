/**
 * 
 */
package com.armedia.acm.plugins.person.model.frevvoxmlmarshal;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.person.model.PersonAlias;

/**
 * @author riste.tutureski
 *
 */
public class PeoplePersonAlias extends PersonAlias {

	private static final long serialVersionUID = 8203957432320850271L;

	public PeoplePersonAlias()
	{
		
	}
	
	public PeoplePersonAlias(PersonAlias personAlias)
	{
		setId(personAlias.getId());
		setAliasType(personAlias.getAliasType());
		setAliasValue(personAlias.getAliasValue());
		setCreated(personAlias.getCreated());
		setCreator(personAlias.getCreator());
	}
	
	@XmlElement(name="peopleAliasId")
	@Override
	public Long getId() {
        return super.getId();
    }
	
	@Override
	public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="peopleAliasType")
	@Override
	public String getAliasType() {
		return super.getAliasType();
	}
	
	@Override
	public void setAliasType(String aliasType) {
		super.setAliasType(aliasType);
	}

	@XmlElement(name="peopleAliasValue")
	@Override
	public String getAliasValue() {
		return super.getAliasValue();
	}
	
	@Override
	public void setAliasValue(String aliasValue) {
		super.setAliasValue(aliasValue);
	}

	@XmlElement(name="peopleAliasDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getCreated() {
		return super.getCreated();
	}
	
	@Override
	public void setCreated(Date created) {
		super.setCreated(created);
	}

	@XmlElement(name="peopleAliasAddedBy")
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
