package com.armedia.acm.services.email.sender.model;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author sasko.tanaskoski
 *
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EmailSenderConfiguration
{

    private String host;

    private Integer port;

    private String type;

    private String encryption;

    private String username;

    @JsonIgnore
    private String password;

    private String userFrom;

    private boolean allowDocuments;

    private boolean allowAttachments;

    private boolean allowHyperlinks;

    private boolean convertDocumentsToPdf;

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return the port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(Integer port)
    {
        this.port = port;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the encryption
     */
    public String getEncryption()
    {
        return encryption;
    }

    /**
     * @param encryption
     *            the encryption to set
     */
    public void setEncryption(String encryption)
    {
        this.encryption = encryption;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    @JsonIgnore
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    @JsonProperty
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the userFrom
     */
    public String getUserFrom()
    {
        return userFrom;
    }

    /**
     * @param userFrom
     *            the userFrom to set
     */
    public void setUserFrom(String userFrom)
    {
        this.userFrom = userFrom;
    }

    /**
     * @return the allowDocuments
     */
    public boolean isAllowDocuments()
    {
        return allowDocuments;
    }

    /**
     * @param allowDocuments
     *            the allowDocuments to set
     */
    public void setAllowDocuments(boolean allowDocuments)
    {
        this.allowDocuments = allowDocuments;
    }

    /**
     * @return the allowAttachments
     */
    public boolean isAllowAttachments()
    {
        return allowAttachments;
    }

    /**
     * @param allowAttachments
     *            the allowAttachments to set
     */
    public void setAllowAttachments(boolean allowAttachments)
    {
        this.allowAttachments = allowAttachments;
    }

    /**
     * @return the allowHyperlinks
     */
    public boolean isAllowHyperlinks()
    {
        return allowHyperlinks;
    }

    /**
     * @param allowHyperlinks
     *            the allowHyperlinks to set
     */
    public void setAllowHyperlinks(boolean allowHyperlinks)
    {
        this.allowHyperlinks = allowHyperlinks;
    }

    public boolean isConvertDocumentsToPdf()
    {
        return convertDocumentsToPdf;
    }

    public void setConvertDocumentsToPdf(boolean convertDocumentsToPdf)
    {
        this.convertDocumentsToPdf = convertDocumentsToPdf;
    }
}
