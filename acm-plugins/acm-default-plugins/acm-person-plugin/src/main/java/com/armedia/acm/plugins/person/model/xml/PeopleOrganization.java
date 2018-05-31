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
import com.armedia.acm.plugins.person.model.Organization;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class PeopleOrganization extends Organization
{

    private static final long serialVersionUID = -5760513489559795890L;

    public PeopleOrganization()
    {

    }

    public PeopleOrganization(Organization organization)
    {
        setOrganizationId(organization.getOrganizationId());
        setOrganizationType(organization.getOrganizationType());
        setOrganizationValue(organization.getOrganizationValue());
        setCreated(organization.getCreated());
        setCreator(organization.getCreator());
    }

    @XmlElement(name = "peopleOrganizationId")
    @Override
    public Long getOrganizationId()
    {
        return super.getOrganizationId();
    }

    @Override
    public void setOrganizationId(Long organizationId)
    {
        super.setOrganizationId(organizationId);
    }

    @XmlElement(name = "peopleOrganizationType")
    @Override
    public String getOrganizationType()
    {
        return super.getOrganizationType();
    }

    @Override
    public void setOrganizationType(String organizationType)
    {
        super.setOrganizationType(organizationType);
    }

    @XmlElement(name = "peopleOrganizationName")
    @Override
    public String getOrganizationValue()
    {
        return super.getOrganizationValue();
    }

    @Override
    public void setOrganizationValue(String organizationValue)
    {
        super.setOrganizationValue(organizationValue);
    }

    @XmlElement(name = "peopleOrganizationDate")
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

    @XmlElement(name = "peopleOrganizationAddedBy")
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
    public Organization returnBase()
    {
        Organization base = new Organization();

        base.setOrganizationId(getOrganizationId());
        base.setOrganizationType(getOrganizationType());
        base.setOrganizationValue(getOrganizationValue());
        base.setCreated(getCreated());
        base.setCreator(getCreator());

        return base;
    }

}
