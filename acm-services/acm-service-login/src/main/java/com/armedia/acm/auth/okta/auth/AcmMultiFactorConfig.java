package com.armedia.acm.auth.okta.auth;

public class AcmMultiFactorConfig
{
    private String defaultLoginTargetUrl;
    private String selectMethodTargetUrl;
    private String verifyMethodTargetUrl;
    private String enrollmentTargetUrl;
    private boolean alwaysUseDefaultUrl;

    public String getDefaultLoginTargetUrl()
    {
        return defaultLoginTargetUrl;
    }

    public void setDefaultLoginTargetUrl(String defaultLoginTargetUrl)
    {
        this.defaultLoginTargetUrl = defaultLoginTargetUrl;
    }

    public String getSelectMethodTargetUrl()
    {
        return selectMethodTargetUrl;
    }

    public void setSelectMethodTargetUrl(String selectMethodTargetUrl)
    {
        this.selectMethodTargetUrl = selectMethodTargetUrl;
    }

    public String getVerifyMethodTargetUrl()
    {
        return verifyMethodTargetUrl;
    }

    public void setVerifyMethodTargetUrl(String verifyMethodTargetUrl)
    {
        this.verifyMethodTargetUrl = verifyMethodTargetUrl;
    }

    public String getEnrollmentTargetUrl()
    {
        return enrollmentTargetUrl;
    }

    public void setEnrollmentTargetUrl(String enrollmentTargetUrl)
    {
        this.enrollmentTargetUrl = enrollmentTargetUrl;
    }

    public boolean isAlwaysUseDefaultUrl()
    {
        return alwaysUseDefaultUrl;
    }

    public void setAlwaysUseDefaultUrl(boolean alwaysUseDefaultUrl)
    {
        this.alwaysUseDefaultUrl = alwaysUseDefaultUrl;
    }
}
