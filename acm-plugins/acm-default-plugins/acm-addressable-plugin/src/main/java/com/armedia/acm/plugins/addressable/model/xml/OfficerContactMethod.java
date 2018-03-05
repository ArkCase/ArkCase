/**
 * 
 */
package com.armedia.acm.plugins.addressable.model.xml;

import com.armedia.acm.plugins.addressable.model.ContactMethod;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class OfficerContactMethod extends ContactMethod
{

    private static final long serialVersionUID = 874876446690087952L;

    public OfficerContactMethod()
    {

    }

    public OfficerContactMethod(ContactMethod contactMethod)
    {
        setId(contactMethod.getId());
        setType(contactMethod.getType());
        setValue(contactMethod.getValue());
    }

    @XmlElement(name = "officerContactId")
    @Override
    public Long getId()
    {
        return super.getId();
    }

    @Override
    public void setId(Long id)
    {
        super.setId(id);
    }

    @XmlElement(name = "officerContactType")
    @Override
    public String getType()
    {
        return super.getType();
    }

    @Override
    public void setType(String type)
    {
        super.setType(type);
    }

    @XmlElement(name = "officerContactValue")
    @Override
    public String getValue()
    {
        return super.getValue();
    }

    @Override
    public void setValue(String value)
    {
        super.setValue(value);
    }

    @Override
    public ContactMethod returnBase()
    {
        ContactMethod base = new ContactMethod();

        base.setId(getId());
        base.setType(getType());
        base.setValue(getValue());

        return base;
    }

}
