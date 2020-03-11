package com.armedia.acm.email.model;

/*-
 * #%L
 * Acm Mail Tools
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = EmailSenderConfig.class)
public class EmailSenderConfig
{
    @JsonProperty("email.sender.type")
    @Value("${email.sender.type}")
    private String type;

    @JsonProperty("email.sender.encryption")
    @Value("${email.sender.encryption}")
    private String encryption;

    @JsonProperty("email.sender.allowAttachments")
    @Value("${email.sender.allowAttachments}")
    private Boolean allowAttachments;

    @JsonProperty("email.sender.allowDocuments")
    @Value("${email.sender.allowDocuments}")
    private Boolean allowDocuments;

    @JsonProperty("email.sender.host")
    @Value("${email.sender.host}")
    private String host;

    @JsonProperty("email.sender.allowHyperlinks")
    @Value("${email.sender.allowHyperlinks}")
    private Boolean allowHyperlinks;

    @JsonProperty("email.sender.userFrom")
    @Value("${email.sender.userFrom}")
    private String userFrom;

    @JsonProperty("email.sender.port")
    @Value("${email.sender.port}")
    private Integer port;

    @JsonProperty("email.sender.username")
    @Value("${email.sender.username}")
    private String username;

    @JsonProperty("email.sender.password")
    @Value("${email.sender.password}")
    private String password;

    @JsonProperty("email.sender.convertDocumentsToPdf")
    @Value("${email.sender.convertDocumentsToPdf}")
    private Boolean convertDocumentsToPdf;
    
    @JsonProperty("email.sender.outgoingEmail.folderName")
    @Value("${email.sender.outgoingEmail.folderName}")
    private String outgoingEmailFolderName;

    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    public String getEncryption()
    {
        return encryption;
    }

    public void setEncryption(String encryption)
    {
        this.encryption = encryption;
    }

    public Boolean getAllowAttachments()
    {
        return allowAttachments;
    }

    public void setAllowAttachments(Boolean allowAttachments)
    {
        this.allowAttachments = allowAttachments;
    }

    public Boolean getAllowDocuments()
    {
        return allowDocuments;
    }

    public void setAllowDocuments(Boolean allowDocuments)
    {
        this.allowDocuments = allowDocuments;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public Boolean getAllowHyperlinks()
    {
        return allowHyperlinks;
    }

    public void setAllowHyperlinks(Boolean allowHyperlinks)
    {
        this.allowHyperlinks = allowHyperlinks;
    }

    public String getUserFrom()
    {
        return userFrom;
    }

    public void setUserFrom(String userFrom)
    {
        this.userFrom = userFrom;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Boolean getConvertDocumentsToPdf()
    {
        return convertDocumentsToPdf;
    }

    public void setConvertDocumentsToPdf(Boolean convertDocumentsToPdf)
    {
        this.convertDocumentsToPdf = convertDocumentsToPdf;
    }

    public String getOutgoingEmailFolderName() 
    {
        return outgoingEmailFolderName;
    }

    public void setOutgoingEmailFolderName(String outgoingEmailFolderName) 
    {
        this.outgoingEmailFolderName = outgoingEmailFolderName;
    }
}
