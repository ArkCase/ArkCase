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
            @RequestParam(value = "page_numbers") Long[] pages,
            @RequestParam(value = "delete_reason") Long deleteReason,
            Authentication auth,
            HttpSession session
    )
    {
        log.debug("User '{}' deleted the following pages '{}' of file '{}'", userId, pages, fileId);

        String user = auth.getName();
        String ip = (String) session.getAttribute("acm_ip_address");

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
