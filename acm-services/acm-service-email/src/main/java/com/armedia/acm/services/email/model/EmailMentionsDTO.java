package com.armedia.acm.services.email.model;

/*-
 * #%L
 * ACM Service: Email
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.util.List;

/**
 * @author ivana.shekerova on 11/14/2018.
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EmailMentionsDTO extends MessageBodyFactory
{
    private String objectType;
    private String subType;
    private Long objectId;
    private String subject;
    private String header;
    private String footer;
    private String body;
    private String textMentioned;
    private String url;
    private List<String> users;
    private List<String> emailAddresses;

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getTextMentioned()
    {
        return textMentioned;
    }

    public void setTextMentioned(String textMentioned)
    {
        this.textMentioned = textMentioned;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<String> getUsers()
    {
        return users;
    }

    public void setUsers(List<String> users)
    {
        this.users = users;
    }

    public List<String> getEmailAddresses()
    {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses)
    {
        this.emailAddresses = emailAddresses;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public String getSubType()
    {
        return subType;
    }

    public void setSubType(String subType)
    {
        this.subType = subType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getFooter()
    {
        return footer;
    }

    public void setFooter(String footer)
    {
        this.footer = footer;
    }
}
