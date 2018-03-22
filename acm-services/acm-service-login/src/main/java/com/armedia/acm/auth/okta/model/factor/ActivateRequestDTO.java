package com.armedia.acm.auth.okta.model.factor;

/**
 * Created by joseph.mcgrady on 11/10/2017.
 */
public class ActivateRequestDTO
{
    private String factorId;
    private String activationCode;

    public String getFactorId()
    {
        return factorId;
    }

    public void setFactorId(String factorId)
    {
        this.factorId = factorId;
    }

    public String getActivationCode()
    {
        return activationCode;
    }

    public void setActivationCode(String activationCode)
    {
        this.activationCode = activationCode;
    }
}