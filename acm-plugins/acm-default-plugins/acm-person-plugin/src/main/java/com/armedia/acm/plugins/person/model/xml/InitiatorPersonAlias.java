/**
 * 
 */
package com.armedia.acm.plugins.person.model.xml;

/*-
 * #%L
 * ACM Default Plugin: Person
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
import com.armedia.acm.plugins.person.model.PersonAlias;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class InitiatorPersonAlias extends PersonAlias
{

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

    @XmlElement(name = "initiatorAliasId")
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

    @XmlElement(name = "initiatorAliasType")
    @Override
    public String getAliasType()
    {
        return super.getAliasType();
    }

    @Override
    public void setAliasType(String aliasType)
    {
        super.setAliasType(aliasType);
    }

    @XmlElement(name = "initiatorAliasValue")
    @Override
    public String getAliasValue()
    {
        return super.getAliasValue();
    }

    @Override
    public void setAliasValue(String aliasValue)
    {
        super.setAliasValue(aliasValue);
    }

    @XmlElement(name = "initiatorAliasDate")
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

    @XmlElement(name = "initiatorAliasAddedBy")
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

    @Override
    public PersonAlias returnBase()
    {
        PersonAlias base = new PersonAlias();

        base.setId(getId());
        base.setAliasType(getAliasType());
        base.setAliasValue(getAliasValue());
        base.setCreated(getCreated());
        base.setCreator(getCreator());

        return base;
    }

}
