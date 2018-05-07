package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by manoj.dhungana 04/10/2017.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class UpdateFileMetadataAPIController implements ApplicationEventPublisherAware
{

    private EcmFileService ecmFileService;
    private ApplicationEventPublisher applicationEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/metadata/{fileId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile updateFile(@RequestBody EcmFile file, @PathVariable("fileId") Long fileId, Authentication authentication)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        if (file == null || file.getFileId() == null || fileId == null || !fileId.equals(file.getFileId()))
        {
            log.error("Invalid incoming file [{}]", file.toString());
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_UPDATE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, null,
                    "Invalid incoming file", null);
        }

        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("oldEcmFile", getEcmFileService().findById(file.getId()));

        // Explicitly set modified to force a save to trigger transformer to reindex data when child objects are changed
        // (e.g participants)
        file.setModified(new Date());

        log.debug("Incoming file id to be updated [{}]", file.getId());
        file = getEcmFileService().updateFile(file);
        if (file != null)
        {

            publishFileUpdatedEvent(file, authentication, true, eventProperties);
            log.info("File update successful [{}]", file);
            return file;
        }
        throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_UPDATE_FILE, EcmFileConstants.OBJECT_FILE_TYPE, fileId,
                "Failed to update file with fileId: " + fileId, null);
    }

    private void publishFileUpdatedEvent(EcmFile file, Authentication authentication, boolean success, Map<String, Object> eventProperties)
    {
        EcmFileUpdatedEvent event;
        event = new EcmFileUpdatedEvent(file, authentication);
        event.setEventProperties(eventProperties);
        event.setSucceeded(success);
        applicationEventPublisher.publishEvent(event);
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
