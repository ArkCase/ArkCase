package com.armedia.acm.auth.okta.model;

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

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum ProviderType
{
    OKTA("OKTA"), RSA("RSA"), SYMANTEC("SYMANTEC"), GOOGLE("GOOGLE"), DUO("DUO"), YUBICO("YUBICO");

    private String provider;

    ProviderType(String provider)
    {
        this.provider = provider;
    }

    public String getProviderType()
    {
        return provider;
    }

    public void setProviderType(String provider)
    {
        this.provider = provider;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("provider", provider)
                .toString();
    }
}
