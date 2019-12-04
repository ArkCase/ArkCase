package com.armedia.acm.plugins.audit.web.api;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Dec, 2019
 */

import com.armedia.acm.plugins.audit.model.AcmGenericApplicationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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