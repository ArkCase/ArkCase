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
 * @author riste.tutureski
 */
public class EcmFileUpdatedEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;

    public EcmFileUpdatedEvent(EcmFile updated, Authentication auth)
    {
        super(updated);
        setEventType("com.armedia.acm.ecm.file.updated");
        setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        setObjectId(updated.getFileId());
        setEventDate(new Date());
        if (auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        setUserId(auth.getName());
        setParentObjectType(updated.getContainer().getContainerObjectType());
        setParentObjectId(updated.getContainer().getContainerObjectId());
    }
}
