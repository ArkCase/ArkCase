/**
 *
 */
package com.armedia.acm.plugins.audit.web.api;

/*-
 * #%L
 * ACM Default Plugin: Audit
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
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Create audit event remotely.
 * <p/>
 * This is just a prototype to show that page deletion in Snowbound can result
 * in raising an event on ArkCase side (https://project.armedia.com/jira/browse/AFDP-1293)
 * <p/>
 * UPDATE:
 * - /api/v1/plugin/audit/event is used for Snowbound generated events
 * <p/>
 * - /api/v1/plugin/audit/generic is used for all other events
 * <p/>
 * <p/>
 * Created by Petar Ilin <petar.ilin@armedia.com> on 19.08.2015.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/audit", "/api/latest/plugin/audit" })
public class PostAuditEventAPIController implements ApplicationEventPublisherAware
{
    private final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;
    private EcmFileDao ecmFileDao;

    @RequestMapping(value = "/event", method = RequestMethod.POST)
    @ResponseBody
    public void createEvent(

            @RequestParam(value = "file_id") Long fileId,
            @RequestParam(value = "user_id") String userId,
            @RequestParam(value = "audit_event_type") String auditEventType,
            @RequestParam(value = "page_numbers", required = false) Long[] pages,
            @RequestParam(value = "delete_reason", required = false) Long deleteReason,
            @RequestParam(value = "reorder_operation", required = false) String pageReorderOperation,
            @RequestParam(value = "document_viewed", required = false) String documentViewed,
            Authentication auth,
            HttpSession session)
    {
        // Common generic audit event information
        String user = auth.getName();
        String ip = (String) session.getAttribute("acm_ip_address");

        log.debug("audit event type: " + auditEventType);

        // Publishes an event of the specified type
        if (auditEventType.equals("delete"))
        {
            createDeleteEvent(user, ip, userId, pages, fileId, deleteReason);
        }
        else if (auditEventType.equals("reorder"))
        {
            createReorderEvent(user, ip, userId, fileId, pageReorderOperation);
        }
        else if (auditEventType.equals("viewed"))
        {
            createViewedEvent(user, ip, userId, fileId, documentViewed);
        }
    }

    /**
     * Remotely trigger publish a generic event.
     *
     * @param type
     *            fully qualified event type
     * @param auth
     *            authentication token
     * @param session
     *            http session object
     */
    @RequestMapping(value = "/generic", method = RequestMethod.POST)
    @ResponseBody
    public void createGenericEvent(
            @RequestParam(value = "type") String type,
            Authentication auth,
            HttpSession session)
    {
        // Common generic audit event information
        String user = auth.getName();
        String ip = (String) session.getAttribute("acm_ip_address");

        log.debug("audit event type [{}]", type);

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent(session);
        event.setIpAddress(ip);
        event.setUserId(user);
        event.setEventDate(new Date());
        event.setEventType(type);
        event.setSucceeded(true);
        applicationEventPublisher.publishEvent(event);
    }

    private void createViewedEvent(String user, String ip, String userId, Long fileId, String documentViewed)
    {
        log.debug("User '{}' viewed document '{}': '{}'", userId, fileId, documentViewed);

        // Obtains the metadata for the file on which the event occurred from the acm3 database
        EcmFile sourceFile = ecmFileDao.find(fileId);

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent(sourceFile);
        event.setIpAddress(ip);
        event.setUserId(user);
        event.setEventDate(new Date());
        event.setEventType("com.armedia.acm.ecm.file.document.viewed");
        event.setObjectId(fileId);
        event.setObjectType("FILE");
        event.setSucceeded(true);
        event.setParentObjectType(sourceFile.getContainer().getContainerObjectType());
        event.setParentObjectId(sourceFile.getContainer().getContainerObjectId());

        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("documentViewed", documentViewed);
        event.setEventProperties(eventProperties);
        applicationEventPublisher.publishEvent(event);
    }

    private void createReorderEvent(String user, String ip, String userId, Long fileId, String pageReorderOperation)
    {
        log.debug("User '{}' executed page reorder on document '{}': '{}'", userId, fileId, pageReorderOperation);

        // Obtains the metadata for the file on which the event occurred from the acm3 database
        EcmFile sourceFile = ecmFileDao.find(fileId);

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent(sourceFile);
        event.setIpAddress(ip);
        event.setUserId(user);
        event.setEventDate(new Date());
        event.setEventType("com.armedia.acm.ecm.file.pages.reorder");
        event.setObjectId(fileId);
        event.setObjectType("FILE");
        event.setSucceeded(true);
        event.setParentObjectType(sourceFile.getContainer().getContainerObjectType());
        event.setParentObjectId(sourceFile.getContainer().getContainerObjectId());

        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("pageReorderOperation", pageReorderOperation);
        event.setEventProperties(eventProperties);
        applicationEventPublisher.publishEvent(event);
    }

    private void createDeleteEvent(String user, String ip, String userId, Long[] pages, Long fileId, Long deleteReason)
    {
        log.debug("User '{}' deleted the following pages '{}' of file '{}'", userId, pages, fileId);

        // Obtains the metadata for the file on which the event occurred from the acm3 database
        EcmFile sourceFile = ecmFileDao.find(fileId);

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent(sourceFile);
        event.setIpAddress(ip);
        event.setUserId(user);
        event.setEventDate(new Date());
        event.setEventType("com.armedia.acm.ecm.file.pages.delete");
        event.setObjectId(fileId);
        event.setObjectType("FILE");
        event.setSucceeded(true);
        event.setParentObjectType(sourceFile.getContainer().getContainerObjectType());
        event.setParentObjectId(sourceFile.getContainer().getContainerObjectId());

        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("deletedPages", pages);
        eventProperties.put("deleteReason", deleteReason);
        event.setEventProperties(eventProperties);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
