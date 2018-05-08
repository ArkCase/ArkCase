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

import com.armedia.acm.plugins.person.model.Person;

import javax.xml.bind.annotation.XmlElement;

import java.time.LocalDate;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class DefendantPerson extends Person implements FrevvoPerson
{

    private static final long serialVersionUID = 601307053343844821L;

    private String type;

    public DefendantPerson()
    {

    }

    public DefendantPerson(Person person)
    {
        setId(person.getId());
        setGivenName(person.getGivenName());
        setFamilyName(person.getFamilyName());
        setDateOfBirth(person.getDateOfBirth());
        setIdentifications(person.getIdentifications());
    }

    @XmlElement(name = "defendantId")
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

    @XmlElement(name = "defendantFirstName")
    @Override
    public String getGivenName()
    {
        return super.getGivenName();
    }

    @Override
    public void setGivenName(String givenName)
    {
        super.setGivenName(givenName);
    }

    @XmlElement(name = "defendantLastName")
    @Override
    public String getFamilyName()
    {
        return super.getFamilyName();
    }

    @Override
    public void setFamilyName(String familyName)
    {
        super.setFamilyName(familyName);
    }

    @XmlElement(name = "defendantDOB")
    @Override
    public LocalDate getDateOfBirth()
    {
        return super.getDateOfBirth();
    }

    @Override
    public void setDateOfBirth(LocalDate dateOfBirth)
    {
        super.setDateOfBirth(dateOfBirth);
    }

    @Override
    @XmlElement(name = "defendantType")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public List<String> getPersonIdentificationKeys()
    {
        return null;
    }

    @Override
    public Person returnBase()
    {
        Person person = new Person();

        person.setId(getId());
        person.setGivenName(getGivenName());
        person.setFamilyName(getFamilyName());
        person.setDateOfBirth(getDateOfBirth());
        person.setIdentifications(getIdentifications());

        return person;
    }

}
