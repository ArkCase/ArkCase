package com.armedia.acm.service.outlook.model;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookContactItem extends OutlookItem
{
    private String surname;
    private String displayName;
    private String companyName;
    private String emailAddress1;
    private String primaryTelephone;
    private String emailAddress2;
    private String completeName;

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getEmailAddress1()
    {
        return emailAddress1;
    }

    public void setEmailAddress1(String emailAddress1)
    {
        this.emailAddress1 = emailAddress1;
    }

    public String getPrimaryTelephone()
    {
        return primaryTelephone;
    }

    public void setPrimaryTelephone(String primaryTelephone)
    {
        this.primaryTelephone = primaryTelephone;
    }

    public String getEmailAddress2()
    {
        return emailAddress2;
    }

    public void setEmailAddress2(String emailAddress2)
    {
        this.emailAddress2 = emailAddress2;
    }

    public String getCompleteName()
    {
        return completeName;
    }

    public void setCompleteName(String completeName)
    {
        this.completeName = completeName;
    }

    @Override
    public String toString()
    {
        return "OutlookContactItem{" +
                "surname='" + surname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", emailAddress1=" + emailAddress1 +
                ", primaryTelephone='" + primaryTelephone + '\'' +
                ", emailAddress2=" + emailAddress2 +
                ", completeName='" + completeName + '\'' +
                "} " + super.toString();
    }
}
