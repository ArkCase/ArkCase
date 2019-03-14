package com.armedia.acm.email.model;

/*-
 * #%L
 * Acm Mail Tools
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class EmailReceiverConfig
{
    @JsonProperty("email.CASE_FILE.user")
    @Value("${email.CASE_FILE.user}")
    private String caseFileUser;

    @JsonIgnore
    @Value("${email.CASE_FILE.password}")
    private String caseFilePassword;

    @JsonProperty("email.create.case.enabled")
    @Value("${email.create.case.enabled}")
    private Boolean createCaseEnabled;

    @JsonProperty("user_complaint")
    @Value("${email.COMPLAINT.user}")
    private String complaintUser;

    @JsonIgnore
    @Value("${email.COMPLAINT.password}")
    private String complaintPassword;

    @JsonProperty("email.create.complaint.enabled")
    @Value("${email.create.complaint.enabled}")
    private Boolean createComplaintEnabled;

    @JsonProperty("email.protocol")
    @Value("${email.protocol}")
    private String protocol;

    @JsonProperty("email.host")
    @Value("${email.host}")
    private String host;

    @JsonProperty("email.fetch.folder")
    @Value("${email.fetch.folder}")
    private String fetchFolder;

    @JsonProperty("email.port")
    @Value("${email.port}")
    private Integer port;

    @JsonProperty("email.debug")
    @Value("${email.debug}")
    private Boolean debug;

    @JsonProperty("email.should-delete-messages")
    @Value("${email.should-delete-messages}")
    private Boolean shouldDeleteMessages;

    @JsonProperty("email.should-mark-messages-as-read")
    @Value("${email.should-mark-messages-as-read}")
    private Boolean shouldMarkMessagesAsRead;

    @JsonProperty("email.max-messages-per-poll")
    @Value("${email.max-messages-per-poll}")
    private Integer maxMessagesPerPoll;

    @JsonProperty("email.fixed-rate")
    @Value("${email.fixed-rate}")
    private Integer fixedRate;

    @JsonProperty("email.userId")
    @Value("${email.userId}")
    private String emailUserId;

    public String getCaseFileUser()
    {
        return caseFileUser;
    }

    public void setCaseFileUser(String caseFileUser)
    {
        this.caseFileUser = caseFileUser;
    }

    public String getCaseFilePassword()
    {
        return caseFilePassword;
    }

    public void setCaseFilePassword(String caseFilePassword)
    {
        this.caseFilePassword = caseFilePassword;
    }

    public Boolean getCreateCaseEnabled()
    {
        return createCaseEnabled;
    }

    public void setCreateCaseEnabled(Boolean createCaseEnabled)
    {
        this.createCaseEnabled = createCaseEnabled;
    }

    public String getComplaintUser()
    {
        return complaintUser;
    }

    public void setComplaintUser(String complaintUser)
    {
        this.complaintUser = complaintUser;
    }

    public String getComplaintPassword()
    {
        return complaintPassword;
    }

    public void setComplaintPassword(String complaintPassword)
    {
        this.complaintPassword = complaintPassword;
    }

    public Boolean getCreateComplaintEnabled()
    {
        return createComplaintEnabled;
    }

    public void setCreateComplaintEnabled(Boolean createComplaintEnabled)
    {
        this.createComplaintEnabled = createComplaintEnabled;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getFetchFolder()
    {
        return fetchFolder;
    }

    public void setFetchFolder(String fetchFolder)
    {
        this.fetchFolder = fetchFolder;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Boolean getDebug()
    {
        return debug;
    }

    public void setDebug(Boolean debug)
    {
        this.debug = debug;
    }

    public Boolean getShouldDeleteMessages()
    {
        return shouldDeleteMessages;
    }

    public void setShouldDeleteMessages(Boolean shouldDeleteMessages)
    {
        this.shouldDeleteMessages = shouldDeleteMessages;
    }

    public Boolean getShouldMarkMessagesAsRead()
    {
        return shouldMarkMessagesAsRead;
    }

    public void setShouldMarkMessagesAsRead(Boolean shouldMarkMessagesAsRead)
    {
        this.shouldMarkMessagesAsRead = shouldMarkMessagesAsRead;
    }

    public Integer getMaxMessagesPerPoll()
    {
        return maxMessagesPerPoll;
    }

    public void setMaxMessagesPerPoll(Integer maxMessagesPerPoll)
    {
        this.maxMessagesPerPoll = maxMessagesPerPoll;
    }

    public Integer getFixedRate()
    {
        return fixedRate;
    }

    public void setFixedRate(Integer fixedRate)
    {
        this.fixedRate = fixedRate;
    }

    public String getEmailUserId()
    {
        return emailUserId;
    }

    public void setEmailUserId(String emailUserId)
    {
        this.emailUserId = emailUserId;
    }
}
