package gov.foia.web.api;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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


import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import gov.foia.model.PublicFlagFiles;
import gov.foia.model.event.EcmFilePublicFlagUpdatedEvent;
import gov.foia.service.PublicFlagService;

@Controller
@RequestMapping({ "/api/v1/service/publicFlag", "/api/latest/service/publicFlag" })
public class PublicFlagAPIController implements ApplicationEventPublisherAware
{

    private final Logger log = LogManager.getLogger(getClass());

    private PublicFlagService publicFlagService;
    private ApplicationEventPublisher eventPublisher;

    @RequestMapping(value = "/{publicStatus}", method = RequestMethod.POST)
    @ResponseBody
    public void updatePublicFlag(@PathVariable("publicStatus") boolean publicFlag,
                                 @RequestBody PublicFlagFiles publicFlagFiles,
                                 HttpServletRequest request,
                                 Authentication authentication) throws AcmObjectNotFoundException, AcmUserActionFailedException {

        try
        {
            getPublicFlagService().updatePublicFlagForFiles(publicFlagFiles.getFileIds(), publicFlagFiles.getFolderIds(), publicFlag);

            EcmFilePublicFlagUpdatedEvent ecmFilePublicFlagUpdatedEvent = new EcmFilePublicFlagUpdatedEvent(publicFlagFiles, authentication.getName(), request.getRemoteAddr());
            ecmFilePublicFlagUpdatedEvent.setSucceeded(true);
            eventPublisher.publishEvent(ecmFilePublicFlagUpdatedEvent);
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            log.error(e.getMessage());
            throw e;
        }
    }


    public PublicFlagService getPublicFlagService()
    {
        return publicFlagService;
    }

    public void setPublicFlagService(PublicFlagService publicFlagService)
    {
        this.publicFlagService = publicFlagService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        eventPublisher = applicationEventPublisher;
    }
}
