package com.armedia.acm.plugins.onlyoffice.service;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.onlyoffice.model.events.DocumentCoEditEvent;
import com.armedia.acm.plugins.onlyoffice.model.events.DocumentCoEditJoinedEvent;
import com.armedia.acm.plugins.onlyoffice.model.events.DocumentCoEditLeaveEvent;
import com.armedia.acm.plugins.onlyoffice.model.events.DocumentCoEditSavedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class OnlyOfficeEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    private void publishEvent(DocumentCoEditEvent documentCoEditEvent)
    {
        eventPublisher.publishEvent(documentCoEditEvent);
    }

    public void publishDocumentCoEditJoinedEvent(EcmFile ecmFile, String userId)
    {
        log.debug("Publishing a user [{}] joins co-edit document [{}] event.", userId, ecmFile.getFileId());
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        DocumentCoEditEvent event = new DocumentCoEditJoinedEvent(ecmFile, userId, ipAddress);
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }

    public void publishDocumentCoEditLeaveEvent(EcmFile ecmFile, String userId)
    {
        log.debug("Publishing a user [{}] leave co-edit document [{}] event.", userId, ecmFile.getFileId());
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        DocumentCoEditEvent event = new DocumentCoEditLeaveEvent(ecmFile, userId, ipAddress);
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }

    public void publishDocumentCoEditSavedEvent(EcmFile ecmFile, String userId)
    {
        log.debug("Publishing a co-edit document [{}] saved event.", ecmFile.getFileId());
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        DocumentCoEditEvent event = new DocumentCoEditSavedEvent(ecmFile, userId, ipAddress);
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }
}
