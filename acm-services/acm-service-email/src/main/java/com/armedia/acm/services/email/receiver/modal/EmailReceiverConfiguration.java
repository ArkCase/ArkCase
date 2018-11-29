package com.armedia.acm.services.email.receiver.modal;

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

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EmailReceiverConfiguration
{

    private String user;

    @JsonIgnore
    private String password;

    private boolean shouldDeleteMessage;

    private boolean shouldMarkMessagesAsRead;

    private int maxMessagePerPoll;

    private long fixedRate;

    private String protocol;

    private String fetchFolder;

    private String host;

    private int port;

    private boolean debug;

    private String user_complaint;

    @JsonIgnore
    private String password_complaint;

    /**
     * @return the email
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user
     *            the email to set
     */
    public void setUser(String user)
    {
        this.user = user;
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
     * @return true/false if should delete message
     */
    public boolean isShouldDeleteMessage()
    {
        return shouldDeleteMessage;
    }

    /**
     * @param shouldDeleteMessage
     *            the shouldDeleteMessage to set
     */
    public void setShouldDeleteMessage(boolean shouldDeleteMessage)
    {
        this.shouldDeleteMessage = shouldDeleteMessage;
    }

    /**
     * @return true/false if should mark messages as read
     */
    public boolean isShouldMarkMessagesAsRead()
    {
        return shouldMarkMessagesAsRead;
    }

    /**
     * @param shouldMarkMessagesAsRead
     *            the shouldMarkMessagesAsRead to set
     */
    public void setShouldMarkMessagesAsRead(boolean shouldMarkMessagesAsRead)
    {
        this.shouldMarkMessagesAsRead = shouldMarkMessagesAsRead;
    }

    /**
     * @return the max messages per poll
     */
    public int getMaxMessagePerPoll()
    {
        return maxMessagePerPoll;
    }

    /**
     * @param maxMessagePerPoll
     *            the maxMessagePerPoll to set
     */
    public void setMaxMessagePerPoll(int maxMessagePerPoll)
    {
        this.maxMessagePerPoll = maxMessagePerPoll;
    }

    /**
     * @return the fixed rate
     */
    public long getFixedRate()
    {
        return fixedRate;
    }

    /**
     * @param fixedRate
     *            the fixedRate to set
     */
    public void setFixedRate(long fixedRate)
    {
        this.fixedRate = fixedRate;
    }

    /**
     * @return the protocol
     */
    public String getProtocol()
    {
        return protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    /**
     * @return the fetch folder
     */
    public String getFetchFolder()
    {
        return fetchFolder;
    }

    /**
     * @param fetchFolder
     *            the fetchFolder to set
     */
    public void setFetchFolder(String fetchFolder)
    {
        this.fetchFolder = fetchFolder;
    }

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
    public int getPort()
    {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @return if the debug is true/false
     */
    public boolean isDebug()
    {
        return debug;
    }

    /**
     * @param debug
     *            the debug to set
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * @return the email address for complaint
     */
    public String getUser_complaint()
    {
        return user_complaint;
    }

    /**
     * @param user_complaint
     *            email address for complaint to set
     */
    public void setUser_complaint(String user_complaint)
    {
        this.user_complaint = user_complaint;
    }

    /**
     * @return the password of complaint email address
     */
    @JsonIgnore
    public String getPassword_complaint()
    {
        return password_complaint;
    }

    /**
     * @param password_complaint
     *            the password for complaint to set
     */
    @JsonProperty
    public void setPassword_complaint(String password_complaint)
    {
        this.password_complaint = password_complaint;
    }
}
