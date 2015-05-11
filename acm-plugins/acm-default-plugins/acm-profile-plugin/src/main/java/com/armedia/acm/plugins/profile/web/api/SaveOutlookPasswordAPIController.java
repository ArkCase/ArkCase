package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.model.UserOrgConstants;
import com.armedia.acm.plugins.profile.service.ProfileEventPublisher;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Controller
@RequestMapping({"/api/v1/plugin/profile/outlook","/api/latest/plugin/profile/outlook"})
public class SaveOutlookPasswordAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ProfileEventPublisher eventPublisher;
    private UserOrgService userOrgService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OutlookDTO saveOutlookPassword(
            @RequestBody OutlookDTO in,
            Authentication authentication,
            HttpSession session
    ) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        Long userProfileId = null;
        UserOrg userOrg = null;
        String ipAddress = (String) session.getAttribute("acm_ip_address");

        try
        {
            Objects.requireNonNull(in, UserOrgConstants.ERROR_PASSWORD_MISSING);
            Objects.requireNonNull(in.getOutlookPassword(), UserOrgConstants.ERROR_PASSWORD_MISSING);
            if ( in.getOutlookPassword().trim().isEmpty() )
            {
                throw new IllegalArgumentException(UserOrgConstants.ERROR_PASSWORD_EMPTY);
            }

            // need the user profile ID for events and error reporting
            userOrg = getUserOrgService().getUserOrgForUserId(authentication.getName());
            userProfileId = userOrg.getUserOrgId();

            getUserOrgService().saveOutlookPassword(authentication, in);

            getEventPublisher().outlookPasswordSavedEvent(userOrg, authentication, ipAddress, true);

            return in;
        }
        catch (AcmObjectNotFoundException e)
        {
            // only happens when there is no user profile
            getEventPublisher().outlookPasswordSavedEvent(null, authentication, ipAddress, false);
            throw e;
        }
        catch (AcmEncryptionException | NullPointerException | IllegalStateException | IllegalArgumentException e)
        {
            log.error("Could not update Outlook password for user: " + e.getMessage(), e);

            getEventPublisher().outlookPasswordSavedEvent(userOrg, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(UserOrgConstants.ACTION_UPDATE_OUTLOOK_PASSWORD,
                    UserOrgConstants.OBJECT_TYPE, userProfileId, e.getMessage(), e);
        }


    }

    public ProfileEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ProfileEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public UserOrgService getUserOrgService() {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService) {
        this.userOrgService = userOrgService;
    }
}
