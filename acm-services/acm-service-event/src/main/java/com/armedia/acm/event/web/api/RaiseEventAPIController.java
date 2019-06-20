package com.armedia.acm.event.web.api;

/*-
 * #%L
 * ACM Service: Events
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

import com.armedia.acm.event.model.AcmGenericApplicationEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Date;

@Controller
@RequestMapping({ "/api/v1/service/event", "/api/latest/service/event" })
public class RaiseEventAPIController implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGenericApplicationEvent raiseEvent(
            @RequestParam(value = "eventKey", required = true) String eventKey,
            @RequestParam(value = "objectType", required = true) String objectType,
            @RequestParam(value = "objectId", required = true) Long objectId,
            @RequestParam(value = "docIds", required = false, defaultValue = "") String docIds,
            Authentication auth,
            HttpSession session)
    {
        log.info("Raising '" + eventKey + "' for '" + objectType + " " + objectId + "'");

        String userId = auth.getName();
        String ip = (String) session.getAttribute("acm_ip_address");

        AcmGenericApplicationEvent event = createObjectEvent(eventKey, objectType, objectId, userId, ip);

        applicationEventPublisher.publishEvent(event);

        if (docIds != null && !docIds.trim().isEmpty())
        {
            createAndPublishDocumentEvents(eventKey, docIds, userId, ip);
        }

        return event;
    }

    private void createAndPublishDocumentEvents(String eventKey, String docIds, String userId, String ip)
    {
        String eventName = "com.armedia.acm.ecm.file." + eventKey.toLowerCase();
        String objectType = "FILE";
        boolean succeeded = true;
        Date eventDate = new Date();

        AcmGenericApplicationEvent event;

        String[] docIdArray = docIds.split(",");
        for (String docIdString : docIdArray)
        {
            if (docIdString != null && !docIdString.trim().isEmpty())
            {
                docIdString = docIdString.trim();

                try
                {
                    Long docId = Long.valueOf(docIdString);

                    event = new AcmGenericApplicationEvent(objectType);

                    event.setObjectType(objectType);
                    event.setSucceeded(succeeded);
                    event.setEventType(eventName);
                    event.setObjectId(docId);
                    event.setEventDate(eventDate);
                    event.setUserId(userId);
                    event.setIpAddress(ip);

                    applicationEventPublisher.publishEvent(event);
                }
                catch (NumberFormatException ne)
                {
                    log.error("Could not raise event for doc id '" + docIdString + "' since the doc id is not a number.");
                }
            }
        }
    }

    private AcmGenericApplicationEvent createObjectEvent(
            String eventKey,
            String objectType,
            Long objectId,
            String userId,
            String ipAddress)
    {
        String eventName = "com.armedia.acm." + objectType.toLowerCase().replace("_", "") + "." + eventKey.toLowerCase();

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent(objectType);

        event.setObjectType(objectType);
        event.setSucceeded(true);
        event.setEventType(eventName);
        event.setObjectId(objectId);
        event.setEventDate(new Date());
        event.setUserId(userId);
        event.setIpAddress(ipAddress);
        return event;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
