package gov.foia.web.api;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;

import gov.foia.model.PublicFlagFiles;
import gov.foia.model.event.EcmFilePublicFlagUpdatedEvent;
import gov.foia.service.PublicFlagService;

@Controller
@RequestMapping({ "/api/v1/service/publicFlag", "/api/latest/service/publicFlag" })
public class PublicFlagAPIController implements ApplicationEventPublisherAware
{

    private final Logger log = LoggerFactory.getLogger(getClass());

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
