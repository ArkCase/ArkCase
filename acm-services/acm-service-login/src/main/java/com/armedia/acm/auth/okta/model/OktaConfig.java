package com.armedia.acm.auth.okta.model;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.springframework.beans.factory.annotation.Value;

public class OktaConfig
{
    @Value("${okta.idpUrl}")
    private String idpUrl;

    @Value("${okta.token}")
    private String token;

    @Value("${okta.defaultLoginTargetUrl}")
    private String defaultLoginTargetUrl;

    @Value("${okta.enrollmentTargetUrl}")
    private String enrollmentTargetUrl;

    @Value("${okta.selectMethodTargetUrl}")
    private String selectMethodTargetUrl;

    @Value("${okta.verifyMethodTargetUrl}")
    private String verifyMethodTargetUrl;

    @Value("true")
    private Boolean alwaysUseDefaultUrl;

    public String getIdpUrl()
    {
        return idpUrl;
    }

    public void setIdpUrl(String idpUrl)
    {
        this.idpUrl = idpUrl;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getDefaultLoginTargetUrl()
    {
        return defaultLoginTargetUrl;
    }

    public void setDefaultLoginTargetUrl(String defaultLoginTargetUrl)
    {
        this.defaultLoginTargetUrl = defaultLoginTargetUrl;
    }

    public String getEnrollmentTargetUrl()
    {
        return enrollmentTargetUrl;
    }

    public void setEnrollmentTargetUrl(String enrollmentTargetUrl)
    {
        this.enrollmentTargetUrl = enrollmentTargetUrl;
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

    public Boolean getAlwaysUseDefaultUrl()
    {
        return alwaysUseDefaultUrl;
    }

    public void setAlwaysUseDefaultUrl(Boolean alwaysUseDefaultUrl)
    {
        this.alwaysUseDefaultUrl = alwaysUseDefaultUrl;
    }
}
