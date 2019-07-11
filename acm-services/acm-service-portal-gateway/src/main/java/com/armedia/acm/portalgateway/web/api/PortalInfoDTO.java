package com.armedia.acm.portalgateway.web.api;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
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

import com.armedia.acm.portalgateway.model.PortalInfo;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 30, 2018
 */
public class PortalInfoDTO
{

    /**
     * Generated unique ID that a portal uses to identify itself to ArkCase. A portal is associated with a given user,
     * so the user portal ID pair must match the configured values.
     */
    private String portalId;

    /**
     * Short summary about the portal.
     */
    private String portalDescription;

    /**
     * The URL where the portal can be reached. It is not used by the system, only by the administrators to check the
     * portal.
     */
    private String portalUrl;

    /**
     * User that a given portal logs in with on ArkCase.
     */
    private String userId;

    private String fullName;

    private String groupName;


    private Boolean portalAuthenticationFlag;

    /**
     *
     */
    public PortalInfoDTO()
    {
    }

    public PortalInfoDTO(PortalInfo portalInfo)
    {
        portalId = portalInfo.getPortalId();
        portalDescription = portalInfo.getPortalDescription();
        portalUrl = portalInfo.getPortalUrl();
        userId = portalInfo.getUser().getUserId();
        fullName = portalInfo.getUser().getFullName();
        groupName = portalInfo.getGroup().getName();
        portalAuthenticationFlag = portalInfo.getPortalAuthenticationFlag();
    }

    /**
     * @return the portalId
     */
    public String getPortalId()
    {
        return portalId;
    }

    /**
     * @param portalId
     *            the portalId to set
     */
    public void setPortalId(String portalId)
    {
        this.portalId = portalId;
    }

    /**
     * @return the portalDescription
     */
    public String getPortalDescription()
    {
        return portalDescription;
    }

    /**
     * @param portalDescription
     *            the portalDescription to set
     */
    public void setPortalDescription(String portalDescription)
    {
        this.portalDescription = portalDescription;
    }

    /**
     * @return the portalUrl
     */
    public String getPortalUrl()
    {
        return portalUrl;
    }

    /**
     * @param portalUrl
     *            the portalUrl to set
     */
    public void setPortalUrl(String portalUrl)
    {
        this.portalUrl = portalUrl;
    }

    /**
     * @return the userId
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * @return the fullName
     */
    public String getFullName()
    {
        return fullName;
    }

    /**
     * @param fullName
     *            the fullName to set
     */
    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    /**
     * @return the groupName
     */
    public String getGroupName()
    {
        return groupName;
    }

    /**
     * @param groupName
     *            the groupName to set
     */
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public Boolean getPortalAuthenticationFlag()
    {
        return portalAuthenticationFlag;
    }

    public void setPortalAuthenticationFlag(Boolean portalAuthenticationFlag)
    {
        this.portalAuthenticationFlag = portalAuthenticationFlag;
    }
}
