/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint.xml;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class PeopleMainInformation extends MainInformation
{

    @XmlElement(name = "peopleTitle")
    @Override
    public String getTitle()
    {
        return super.getTitle();
    }

    @Override
    public void setTitle(String title)
    {
        super.setTitle(title);
    }

    @XmlElement(name = "peopleAnonimuos")
    @Override
    public String getAnonymous()
    {
        return super.getAnonymous();
    }

    @Override
    public void setAnonymous(String anonymous)
    {
        super.setAnonymous(anonymous);
    }

    @XmlElement(name = "peopleFirstName")
    @Override
    public String getFirstName()
    {
        return super.getFirstName();
    }

    @Override
    public void setFirstName(String firstName)
    {
        super.setFirstName(firstName);
    }

    @XmlElement(name = "peopleLastName")
    @Override
    public String getLastName()
    {
        return super.getLastName();
    }

    @Override
    public void setLastName(String lastName)
    {
        super.setLastName(lastName);
    }

    @XmlElement(name = "peopleType")
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

    @XmlElement(name = "peopleDescription")
    @Override
    public String getDescription()
    {
        return super.getDescription();
    }

    @Override
    public void setDescription(String description)
    {
        super.setDescription(description);
    }

}
