package com.armedia.acm.objectdiff.model;

/*-
 * #%L
 * Tool Integrations: Object Diff Util
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

import com.armedia.acm.core.AcmObject;

import java.io.Serializable;
import java.util.List;

public class TestPerson implements AcmObject, Serializable
{
    private Long id;
    private String name;
    private String lastName;
    private String toBeIgnored;
    private TestAttribute defaultAttribute;
    private List<TestAttribute> attributeList;

    @Override
    public String getObjectType()
    {
        return "TEST_PERSON";
    }

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getToBeIgnored()
    {
        return toBeIgnored;
    }

    public void setToBeIgnored(String toBeIgnored)
    {
        this.toBeIgnored = toBeIgnored;
    }

    public List<TestAttribute> getAttributeList()
    {
        return attributeList;
    }

    public void setAttributeList(List<TestAttribute> attributeList)
    {
        this.attributeList = attributeList;
    }

    public TestAttribute getDefaultAttribute()
    {
        return defaultAttribute;
    }

    public void setDefaultAttribute(TestAttribute defaultAttribute)
    {
        this.defaultAttribute = defaultAttribute;
    }
}
