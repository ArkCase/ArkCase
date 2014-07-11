package com.armedia.acm.services.dataaccess.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlEvent;
import com.armedia.acm.services.dataaccess.model.enums.AccessControlDecision;
import com.armedia.acm.services.dataaccess.service.DataAccessDefaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = { "/api/v1/plugin/dataaccess", "/api/latest/plugin/dataaccess" } )
public class UpdateAccessControlDefaultController implements ApplicationEventPublisherAware
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private DataAccessDefaultService dataAccessDefaultService;
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Updates an existing default access control.
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/default/{defaultAccessId}",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmAccessControlDefault postDefault(
            @PathVariable(value = "defaultAccessId") Long defaultAccessId,
            @RequestBody AcmAccessControlDefault defaultAccess,
            HttpSession session,
            Authentication authentication
    )
    throws AcmUserActionFailedException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("User '" + authentication.getName() + "' is updating default access with id '" + defaultAccessId + "'");
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");

        AcmAccessControlDefault updated = null;
        try
        {
            AccessControlDecision.validateValue(defaultAccess.getAccessDecision());

            updated = getDataAccessDefaultService().save(defaultAccessId, defaultAccess, authentication);
            AcmAccessControlEvent event = new AcmAccessControlEvent(updated, "updated", authentication.getName(),
                    true, ipAddress);
            getApplicationEventPublisher().publishEvent(event);
        }
        catch (AcmUserActionFailedException e)
        {
            AcmAccessControlEvent event = new AcmAccessControlEvent(defaultAccess, "updated", authentication.getName(),
                    false, ipAddress);
            getApplicationEventPublisher().publishEvent(event);
            throw e;
        }
        catch (Exception e)
        {
            log.error("Exception updating default access: " + e.getMessage(), e);
            AcmAccessControlEvent event = new AcmAccessControlEvent(defaultAccess, "updated", authentication.getName(),
                false, ipAddress);
            getApplicationEventPublisher().publishEvent(event);
            throw new AcmUserActionFailedException("update", "default access", defaultAccessId,
                    e.getMessage(), e);
        }

        return updated;

    }

    public DataAccessDefaultService getDataAccessDefaultService()
    {
        return dataAccessDefaultService;
    }

    public void setDataAccessDefaultService(DataAccessDefaultService dataAccessDefaultService)
    {
        this.dataAccessDefaultService = dataAccessDefaultService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }
}
