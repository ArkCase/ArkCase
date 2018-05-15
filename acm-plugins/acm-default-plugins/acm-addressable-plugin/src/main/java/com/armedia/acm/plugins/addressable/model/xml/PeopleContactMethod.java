/**
 * 
 */
package com.armedia.acm.plugins.addressable.model.xml;

/*-
 * #%L
 * ACM Default Plugin: Addressable
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class PeopleContactMethod extends ContactMethod
{

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

    @XmlElement(name = "peopleDeviceId")
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

    @XmlElement(name = "peopleDeviceDate")
    @XmlJavaTypeAdapter(value = DateFrevvoAdapter.class)
    @Override
    public Date getCreated()
    {
        return super.getCreated();
    }

    @Override
    public void setCreated(Date created)
    {
        super.setCreated(created);
    }

    @XmlElement(name = "peopleDeviceAddedBy")
    @Override
    public String getCreator()
    {
        return super.getCreator();
    }

    @Override
    public void setCreator(String creator)
    {
        super.setCreator(creator);
    }

    @XmlElement(name = "peopleDeviceType")
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

    @XmlElement(name = "peopleDeviceValue")
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
        base.setCreated(getCreated());
        base.setCreator(getCreator());
        base.setType(getType());
        base.setValue(getValue());

        return base;
    }

}
