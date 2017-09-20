package com.armedia.acm.form.config.xml;

import com.armedia.acm.form.config.Item;

import javax.xml.bind.annotation.XmlElement;

public class PeopleItem extends Item
{

    private String personType;
    private Long personAssociationId;

    @XmlElement(name="personId")
    @Override public Long getId()
    {
        return super.getId();
    }

    @Override public void setId(Long id)
    {
        super.setId(id);
    }

    @XmlElement(name="personFullName")
    @Override public String getValue()
    {
        return super.getValue();
    }

    @Override public void setValue(String value)
    {
        super.setValue(value);
    }

    @XmlElement(name="personType")
    public String getPersonType()
    {
        return personType;
    }

    public void setPersonType(String personType)
    {
        this.personType = personType;
    }

    @XmlElement(name="personAssociationId")
    public Long getPersonAssociationId()
    {
        return personAssociationId;
    }

    public void setPersonAssociationId(Long personAssociationId)
    {
        this.personAssociationId = personAssociationId;
    }
}
