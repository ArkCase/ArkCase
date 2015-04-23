package com.armedia.acm.service.outlook.model;

/**
 * Created by armdev on 4/20/15.
 */
public class AcmOutlookUser
{
    private final String userId;
    private final String emailAddress;
    private final String outlookPassword;

    public AcmOutlookUser(String userId, String emailAddress, String outlookPassword)
    {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.outlookPassword = outlookPassword;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public String getOutlookPassword()
    {
        return outlookPassword;
    }
}
