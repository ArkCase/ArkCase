package com.armedia.acm.services.exemption.model;

/*-
 * #%L
 * ACM Service: Exemption
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by ana.serafimoska
 */
public class DocumentRedactionEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishExemptionCodeCreatedEvent(EcmFile source)
    {
        DocumentCodeCreatedEvent documentCodeCreatedEvent = new DocumentCodeCreatedEvent(source);
        documentCodeCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        documentCodeCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        documentCodeCreatedEvent.setParentObjectId(source.getParentObjectId());
        documentCodeCreatedEvent.setParentObjectType(source.getParentObjectType());
        documentCodeCreatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(documentCodeCreatedEvent);
    }

    public void publishExemptionCodeDeletedEvent(EcmFile source)
    {
        DocumentCodeDeletedEvent documentCodeDeletedEvent = new DocumentCodeDeletedEvent(source);
        documentCodeDeletedEvent.setUserId(AuthenticationUtils.getUsername());
        documentCodeDeletedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        documentCodeDeletedEvent.setParentObjectId(source.getParentObjectId());
        documentCodeDeletedEvent.setParentObjectType(source.getParentObjectType());
        documentCodeDeletedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(documentCodeDeletedEvent);

    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
