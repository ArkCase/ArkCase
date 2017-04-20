package com.armedia.acm.services.email.sender.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author sasko.tanaskoski
 *
 */
@JsonInclude(Include.NON_NULL)
public class EmailSenderConfiguration
{

    private String host;

    private Integer port;

    private String type;

    private String encryption;

    private String username;

    private String password;

    private String userFrom;

    private boolean allowSending;

    private boolean allowAttachments;

    private boolean allowHyperlinks;

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
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
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
     * @return the allowSending
     */
    public boolean isAllowSending()
    {
        return allowSending;
    }

    /**
     * @param allowSending
     *            the allowSending to set
     */
    public void setAllowSending(boolean allowSending)
    {
        this.allowSending = allowSending;
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

}
