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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = EmailReceiverConfig.class)
public class EmailReceiverConfig
{
    @JsonProperty("email.CASE_FILE.user")
    @Value("${email.CASE_FILE.user}")
    private String caseFileUser;

    @JsonProperty("email.CASE_FILE.password")
    @Value("${email.CASE_FILE.password}")
    private String caseFilePassword;

    @JsonProperty("email.create.case.enabled")
    @Value("${email.create.case.enabled}")
    private Boolean createCaseEnabled;

    @JsonProperty("email.COMPLAINT.user")
    @Value("${email.COMPLAINT.user}")
    private String complaintUser;

    @JsonProperty("email.COMPLAINT.password")
    @Value("${email.COMPLAINT.password}")
    private String complaintPassword;

    @JsonProperty("email.create.complaint.enabled")
    @Value("${email.create.complaint.enabled}")
    private Boolean createComplaintEnabled;

    @Value("${email.protocol}")
    private String protocol;

    @Value("${email.host}")
    private String host;

    @Value("${email.fetch.folder}")
    private String fetchFolder;

    @Value("${email.port}")
    private Integer port;

    @Value("${email.debug}")
    private Boolean debug;

    @Value("${email.should-delete-messages}")
    private Boolean shouldDeleteMessages;

    @Value("${email.should-mark-messages-as-read}")
    private Boolean shouldMarkMessagesAsRead;

    @Value("${email.max-messages-per-poll}")
    private Integer maxMessagesPerPoll;

    @Value("${email.fixed-rate}")
    private Integer fixedRate;

    @Value("${email.userId}")
    private String emailUserId;

    @JsonProperty("email.enableBurstingAttachments")
    @Value("${email.enableBurstingAttachments}")
    private Boolean enableBurstingAttachments;

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

    @JsonIgnore
    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    @JsonIgnore
    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    @JsonIgnore
    public String getFetchFolder()
    {
        return fetchFolder;
    }

    public void setFetchFolder(String fetchFolder)
    {
        this.fetchFolder = fetchFolder;
    }

    @JsonIgnore
    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    @JsonIgnore
    public Boolean getDebug()
    {
        return debug;
    }

    public void setDebug(Boolean debug)
    {
        this.debug = debug;
    }

    @JsonIgnore
    public Boolean getShouldDeleteMessages()
    {
        return shouldDeleteMessages;
    }

    public void setShouldDeleteMessages(Boolean shouldDeleteMessages)
    {
        this.shouldDeleteMessages = shouldDeleteMessages;
    }

    @JsonIgnore
    public Boolean getShouldMarkMessagesAsRead()
    {
        return shouldMarkMessagesAsRead;
    }

    public void setShouldMarkMessagesAsRead(Boolean shouldMarkMessagesAsRead)
    {
        this.shouldMarkMessagesAsRead = shouldMarkMessagesAsRead;
    }

    @JsonIgnore
    public Integer getMaxMessagesPerPoll()
    {
        return maxMessagesPerPoll;
    }

    public void setMaxMessagesPerPoll(Integer maxMessagesPerPoll)
    {
        this.maxMessagesPerPoll = maxMessagesPerPoll;
    }

    @JsonIgnore
    public Integer getFixedRate()
    {
        return fixedRate;
    }

    public void setFixedRate(Integer fixedRate)
    {
        this.fixedRate = fixedRate;
    }

    @JsonIgnore
    public String getEmailUserId()
    {
        return emailUserId;
    }

    public void setEmailUserId(String emailUserId)
    {
        this.emailUserId = emailUserId;
    }

    public Boolean getEnableBurstingAttachments() 
    {
        return enableBurstingAttachments;
    }

    public void setEnableBurstingAttachments(Boolean enableBurstingAttachments) 
    {
        this.enableBurstingAttachments = enableBurstingAttachments;
    }
}
