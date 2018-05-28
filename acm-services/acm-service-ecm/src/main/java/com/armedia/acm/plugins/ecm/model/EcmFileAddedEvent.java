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

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileAddedEvent extends AcmEvent
{
    private String ecmFileId;
    private EcmFile source;

    public EcmFileAddedEvent(EcmFile uploaded, Authentication authentication)
    {
        super(uploaded);

        setSource(uploaded);

        String fileType = uploaded.getFileType();
        String ft = "";

        if (StringUtils.isNotEmpty(fileType) && fileType.endsWith("_xml"))
        {
            ft = ".xml";
        }

        setEventType("com.armedia.acm.ecm.file.added" + ft);
        setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        setObjectId(uploaded.getFileId());
        setEventDate(new Date());
        setUserId(uploaded.getModifier());
        setEcmFileId(uploaded.getVersionSeriesId());
        setParentObjectType(uploaded.getContainer().getContainerObjectType());
        setParentObjectId(uploaded.getContainer().getContainerObjectId());

        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
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
