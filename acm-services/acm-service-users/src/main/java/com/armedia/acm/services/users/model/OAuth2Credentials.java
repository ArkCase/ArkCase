package com.armedia.acm.services.users.model;

public class OAuth2Credentials
{
    private String registrationId;

    private String clientId;

    private String clientSecret;

    private String tokenUri;

    private String systemUserEmail;

    private String systemUserPassword;

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getClientSecret()
    {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
    }

    public String getTokenUri()
    {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri)
    {
        this.tokenUri = tokenUri;
    }

    public String getSystemUserEmail()
    {
        return systemUserEmail;
    }

    public void setSystemUserEmail(String systemUserEmail)
    {
        this.systemUserEmail = systemUserEmail;
    }

    public String getSystemUserPassword()
    {
        return systemUserPassword;
    }

    public void setSystemUserPassword(String systemUserPassword)
    {
        this.systemUserPassword = systemUserPassword;
    }

}
