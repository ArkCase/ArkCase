/**
 *
 */
package com.armedia.acm.plugins.audit.web.api;

import com.armedia.acm.event.model.AcmGenericApplicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p>
 * This is just a prototype to show that page deletion in Snowbound can result
 * in raising an event on ArkCase side (https://project.armedia.com/jira/browse/AFDP-1293)
 * <p>
 * Created by Petar Ilin <petar.ilin@armedia.com> on 19.08.2015.
 */
@Controller
@RequestMapping({"/api/v1/plugin/audit", "/api/latest/plugin/audit"})
public class PostAuditEventAPIController implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    private final Logger log = LoggerFactory.getLogger(getClass());

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
            HttpSession session
    )
    {
        // Common generic audit event information
        String user = auth.getName();
        String ip = (String) session.getAttribute("acm_ip_address");

        log.debug("audit event type: " + auditEventType);

        // Publishes an event of the specified type
        if (auditEventType.equals("delete")) {
            createDeleteEvent(user, ip, userId, pages, fileId, deleteReason);
        } else if (auditEventType.equals("reorder")) {
            createReorderEvent(user, ip, userId, fileId, pageReorderOperation);
        } else if (auditEventType.equals("viewed")) {
            createViewedEvent(user, ip, userId, fileId, documentViewed);
        }
    }

    private void createViewedEvent(String user, String ip, String userId, Long fileId, String documentViewed)
    {
        log.debug("User '{}' viewed document '{}': '{}'", userId, fileId, documentViewed);

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent("FILE");
        event.setIpAddress(ip);
        event.setUserId(user);
        event.setEventDate(new Date());
        event.setEventType("com.armedia.acm.ecm.file.document.viewed");
        event.setObjectId(fileId);

        Map<String, Object> eventProperties = new HashMap<String, Object>();
        eventProperties.put("documentViewed", documentViewed);
        event.setEventProperties(eventProperties);
        applicationEventPublisher.publishEvent(event);
    }

    private void createReorderEvent(String user, String ip, String userId, Long fileId, String pageReorderOperation)
    {
        log.debug("User '{}' executed page reorder on document '{}': '{}'", userId, fileId, pageReorderOperation);

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent("FILE");
        event.setIpAddress(ip);
        event.setUserId(user);
        event.setEventDate(new Date());
        event.setEventType("com.armedia.acm.ecm.file.pages.reorder");
        event.setObjectId(fileId);

        Map<String, Object> eventProperties = new HashMap<String, Object>();
        eventProperties.put("pageReorderOperation", pageReorderOperation);
        event.setEventProperties(eventProperties);
        applicationEventPublisher.publishEvent(event);
    }

    private void createDeleteEvent(String user, String ip, String userId, Long[] pages, Long fileId, Long deleteReason)
    {
        log.debug("User '{}' deleted the following pages '{}' of file '{}'", userId, pages, fileId);

        AcmGenericApplicationEvent event = new AcmGenericApplicationEvent("FILE");
        event.setIpAddress(ip);
        event.setUserId(user);
        event.setEventDate(new Date());
        event.setEventType("com.armedia.acm.ecm.file.pages.delete");
        event.setObjectId(fileId);

        Map<String, Object> eventProperties = new HashMap<String, Object>();
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
}
