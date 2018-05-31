package com.armedia.acm.form.config.xml;

/*-
 * #%L
 * ACM Service: Form Configuration
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

import com.armedia.acm.form.config.Item;

import javax.xml.bind.annotation.XmlElement;

public class PersonItem extends Item
{

    private String personType;
    private Long personAssociationId;

    @XmlElement(name = "personId")
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

    @XmlElement(name = "personFullName")
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

    @XmlElement(name = "personType")
    public String getPersonType()
    {
        return personType;
    }

    public void setPersonType(String personType)
    {
        this.personType = personType;
    }

    @XmlElement(name = "personAssociationId")
    public Long getPersonAssociationId()
    {
        return personAssociationId;
    }

    public void setPersonAssociationId(Long personAssociationId)
    {
        this.personAssociationId = personAssociationId;
    }
}
