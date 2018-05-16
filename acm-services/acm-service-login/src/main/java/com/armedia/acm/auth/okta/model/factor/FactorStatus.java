package com.armedia.acm.auth.okta.model.factor;

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

public enum FactorStatus
{
    NOT_SETUP("NOT_SETUP"),
    PENDING_ACTIVATION("PENDING_ACTIVATION"),
    ENROLLED("ENROLLED"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    EXPIRED("EXPIRED");

    private String status;

    FactorStatus(String status)
    {
        this.status = status;
    }

    public static boolean isActive(FactorStatus factorStatus)
    {
        return !NOT_SETUP.equals(factorStatus) && !PENDING_ACTIVATION.equals(factorStatus) && !INACTIVE.equals(factorStatus)
                && !EXPIRED.equals(factorStatus);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("status", status)
                .toString();
    }
}
