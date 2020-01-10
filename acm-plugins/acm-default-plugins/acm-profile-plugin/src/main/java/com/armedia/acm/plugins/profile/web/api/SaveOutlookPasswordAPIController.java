package com.armedia.acm.plugins.profile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Profile
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

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.profile.service.ProfileEventPublisher;
import com.armedia.acm.service.outlook.model.OutlookConstants;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.OutlookPassword;
import com.armedia.acm.service.outlook.service.OutlookService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
@RequestMapping({ "/api/v1/plugin/profile/savepassword", "/api/latest/plugin/profile/savepassword" })
public class SaveOutlookPasswordAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private ProfileEventPublisher eventPublisher;
    private OutlookService outlookService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OutlookDTO saveOutlookPassword(@RequestBody OutlookDTO in, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        OutlookPassword outlookPassword = null;
        String ipAddress = (String) session.getAttribute("acm_ip_address");

        try
        {
            Objects.requireNonNull(in, OutlookConstants.ERROR_PASSWORD_MISSING);
            Objects.requireNonNull(in.getOutlookPassword(), OutlookConstants.ERROR_PASSWORD_MISSING);
            if (in.getOutlookPassword().trim().isEmpty())
            {
                throw new IllegalArgumentException(OutlookConstants.ERROR_PASSWORD_EMPTY);
            }

            getOutlookService().saveOutlookPassword(authentication, in);

            getEventPublisher().outlookPasswordSavedEvent(outlookPassword, authentication, ipAddress, true);

            return in;
        }
        catch (AcmEncryptionException | NullPointerException | IllegalStateException | IllegalArgumentException e)
        {
            log.error("Could not update Outlook password for user: " + e.getMessage(), e);

            getEventPublisher().outlookPasswordSavedEvent(outlookPassword, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(OutlookConstants.ACTION_UPDATE_OUTLOOK_PASSWORD, OutlookConstants.OBJECT_TYPE, null,
                    e.getMessage(), e);
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

    public OutlookService getOutlookService()
    {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService)
    {
        this.outlookService = outlookService;
    }
}
