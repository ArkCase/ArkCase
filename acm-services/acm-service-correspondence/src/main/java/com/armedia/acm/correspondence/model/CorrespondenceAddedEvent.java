package com.armedia.acm.correspondence.model;

/*-
 * #%L
 * ACM Service: Correspondence Library
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
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.springframework.security.core.Authentication;

import java.util.Date;

public class CorrespondenceAddedEvent extends AcmEvent
{

    public static final String EVENT_TYPE = "com.armedia.acm.correspondence.file.added";

    public CorrespondenceAddedEvent(EcmFile source, Authentication authentication, boolean succeeded)
    {
        super(source);

        setEventType(EVENT_TYPE);
        setObjectType("FILE");
        setObjectId(source.getFileId());
        setEventDate(new Date());
        setUserId(source.getModifier());
        setSucceeded(succeeded);
        setParentObjectId(source.getContainer().getContainerObjectId());
        setParentObjectType(source.getContainer().getContainerObjectType());

        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }

    }
}
