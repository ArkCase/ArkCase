package com.armedia.acm.web.api.service;

/**
 * Created by dragan.simonovski on 05/11/2016.
 */
public class LoginWarningMessageServiceImpl implements LoginWarningMessageService
{
    private Boolean warningEnabled;
    private String warningMessage;

    @Override
    public boolean isEnabled()
    {
        return warningEnabled == null ? false : warningEnabled.booleanValue();
    }

    @Override
    public String getMessage()
    {
        return warningMessage == null ? "" : warningMessage;
    }

    public void setWarningEnabled(String warningEnabled)
    {
        this.warningEnabled = Boolean.parseBoolean(warningEnabled == null ? "false" : warningEnabled);
    }

    public void setWarningMessage(String warningMessage)
    {
        this.warningMessage = warningMessage;
    }
}
