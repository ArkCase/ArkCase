package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.model.AcmEvent;

import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFolderDeclareRequestEvent extends AcmEvent
{
    private String parentObjectType;
    private Long parentObjectId;
    private String parentObjectName;
    private Long folderId;
    private AcmCmisObjectList source;
    private AcmContainer container;

    public EcmFolderDeclareRequestEvent(AcmCmisObjectList acmCmisObjectList, AcmContainer acmContainer, Authentication authentication)
    {
        super(acmCmisObjectList);

        setSource(acmCmisObjectList);

        setEventType("com.armedia.acm.ecm.folder.declare.requested");
        setObjectType("FOLDER");
        setObjectId(acmCmisObjectList.getFolderId());
        setEventDate(new Date());
        setUserId(authentication.getName());
        setParentObjectType(acmCmisObjectList.getContainerObjectType());
        setParentObjectId(acmCmisObjectList.getContainerObjectId());
        setFolderId(acmCmisObjectList.getFolderId());
        setContainer(acmContainer);
        setParentObjectName(acmContainer.getContainerObjectTitle());
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
    }

    @Override
    public String getParentObjectType()
    {
        return parentObjectType;
    }

    @Override
    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    @Override
    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    @Override
    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public AcmContainer getContainer()
    {
        return container;
    }

    public void setContainer(AcmContainer container)
    {
        this.container = container;
    }

    public Long getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Long folderId)
    {
        this.folderId = folderId;
    }

    @Override
    public String getParentObjectName()
    {
        return parentObjectName;
    }

    @Override
    public void setParentObjectName(String parentObjectName)
    {
        this.parentObjectName = parentObjectName;
    }

    @Override
    public AcmCmisObjectList getSource()
    {
        return source;
    }

    public void setSource(AcmCmisObjectList source)
    {
        this.source = source;
    }
}
