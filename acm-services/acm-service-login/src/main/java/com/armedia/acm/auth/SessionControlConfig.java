package com.armedia.acm.auth;

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

public class SessionControlConfig
{
    /**
     * Number of concurrent session allowed.
     * Set to -1 to allow unlimited sessions.
     */
    @Value("${sessionControl.exceptionIfMaximumExceeded}")
    private Boolean exceptionIfMaximumExceeded;

    /**
     * Determines whether the user should be prevented from opening more sessions than allowed.
     * If set to true, the user authenticating will be prevented from authenticating.
     * If set to false, the user that has already authenticated will be forcibly logged out.
     */
    @Value("${sessionControl.maximumSessions}")
    private Integer maximumSessions;

    public Boolean getExceptionIfMaximumExceeded()
    {
        return exceptionIfMaximumExceeded;
    }

    public void setExceptionIfMaximumExceeded(Boolean exceptionIfMaximumExceeded)
    {
        this.exceptionIfMaximumExceeded = exceptionIfMaximumExceeded;
    }

    public Integer getMaximumSessions()
    {
        return maximumSessions;
    }

    public void setMaximumSessions(Integer maximumSessions)
    {
        this.maximumSessions = maximumSessions;
    }
}
