package com.armedia.acm.plugins.wopi.model;

/*-
 * #%L
 * ACM Service: Wopi service
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

public class WopiSessionInfo
{
    private final String accessToken;
    private final String userId;
    private final String fileId;
    private final boolean readOnly;
    private final boolean userCanWrite;

    public WopiSessionInfo(String accessToken, String userId, String fileId, boolean readOnly, boolean userCanWrite)
    {
        this.accessToken = accessToken;
        this.userId = userId;
        this.fileId = fileId;
        this.readOnly = readOnly;
        this.userCanWrite = userCanWrite;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getFileId()
    {
        return fileId;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public boolean isUserCanWrite()
    {
        return userCanWrite;
    }
}
