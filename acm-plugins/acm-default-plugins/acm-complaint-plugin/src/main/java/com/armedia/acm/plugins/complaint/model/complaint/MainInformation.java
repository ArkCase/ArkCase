/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

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

import javax.xml.bind.annotation.XmlTransient;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class MainInformation
{

    private String title;
    private List<String> titles;
    private String anonymous;
    private String firstName;
    private String lastName;
    private String type;
    private List<String> types;
    private String description;

    @XmlTransient
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @XmlTransient
    public List<String> getTitles()
    {
        return titles;
    }

    public void setTitles(List<String> titles)
    {
        this.titles = titles;
    }

    @XmlTransient
    public String getAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(String anonymous)
    {
        this.anonymous = anonymous;
    }

    @XmlTransient
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    @XmlTransient
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    @XmlTransient
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlTransient
    public List<String> getTypes()
    {
        return types;
    }

    public void setTypes(List<String> types)
    {
        this.types = types;
    }

    @XmlTransient
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}
