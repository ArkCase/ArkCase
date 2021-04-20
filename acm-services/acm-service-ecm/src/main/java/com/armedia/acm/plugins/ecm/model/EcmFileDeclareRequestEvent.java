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

import java.util.Date;

import org.springframework.security.core.Authentication;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.model.AcmEvent;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileDeclareRequestEvent extends AcmEvent
{
    private String parentObjectType;
    private Long parentObjectId;
    private String parentObjectName;
    private String ecmFileId;
    private EcmFile source;

    public EcmFileDeclareRequestEvent(EcmFile ecmFile, Authentication authentication)
    {
        super(ecmFile);

        setSource(ecmFile);

        setEventType("com.armedia.acm.ecm.file.declare.requested");
        setObjectType("FILE");
        setObjectId(ecmFile.getFileId());
        setEventDate(new Date());
        setUserId(ecmFile.getModifier());
        setEcmFileId(ecmFile.getVersionSeriesId());
        setParentObjectName(ecmFile.getContainer().getContainerObjectTitle());
        setParentObjectType(ecmFile.getContainer().getContainerObjectType());
        setParentObjectId(ecmFile.getContainer().getContainerObjectId());

        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }

    }

    public EcmFileDeclareRequestEvent(EcmFile ecmFile, Boolean succeeded, Authentication authentication)
    {
        this(ecmFile, authentication);
        setSucceeded(succeeded);
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

    public String getEcmFileId()
    {
        return ecmFileId;
    }

    public void setEcmFileId(String ecmFileId)
    {
        this.ecmFileId = ecmFileId;
    }

    @Override
    public EcmFile getSource()
    {
        return source;
    }

    public void setSource(EcmFile source)
    {
        this.source = source;
    }
}
