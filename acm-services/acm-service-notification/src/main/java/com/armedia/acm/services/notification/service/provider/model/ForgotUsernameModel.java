package com.armedia.acm.services.notification.service.provider.model;

public class ForgotUsernameModel
{
    private final int systemAccountsNum;

    private final String systemAccountsCommaSeparated;

    public ForgotUsernameModel(int systemAccountsNum, String systemAccountsCommaSeparated)
    {
        this.systemAccountsNum = systemAccountsNum;
        this.systemAccountsCommaSeparated = systemAccountsCommaSeparated;
    }

    public int getSystemAccountsNum()
    {
        return systemAccountsNum;
    }

    public String getSystemAccountsCommaSeparated()
    {
        return systemAccountsCommaSeparated;
    }

}
