package com.armedia.acm.auth.okta.model.factor;

/**
 * Created by joseph.mcgrady on 11/10/2017.
 */
public class VerifyRequestDTO
{
    private String factorId;
    private String userId;
    private String href;

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}