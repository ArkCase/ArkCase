/**
 * 
 */
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

import com.armedia.acm.services.users.model.AcmUser;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class ProsecutorUser extends AcmUser
{

    private static final long serialVersionUID = 1L;

    private String location;
    private String phone;

    public ProsecutorUser()
    {

    }

    public ProsecutorUser(AcmUser user)
    {
        setUserId(user.getUserId());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setMail(user.getMail());
    }

    @XmlElement(name = "prosecutorId")
    @Override
    public String getUserId()
    {
        return super.getUserId();
    }

    @Override
    public void setUserId(String userId)
    {
        super.setUserId(userId);
    }

    @XmlElement(name = "prosecutorFirstName")
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

    @XmlElement(name = "prosecutorLastName")
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

    @XmlElement(name = "prosecutorEmail")
    @Override
    public String getMail()
    {
        return super.getMail();
    }

    @Override
    public void setMail(String mail)
    {
        super.setMail(mail);
    }

    @XmlElement(name = "prosecutorLocation")
    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    @XmlElement(name = "prosecutorPhone")
    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

}
