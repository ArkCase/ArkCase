package com.armedia.acm.auth.okta.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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
