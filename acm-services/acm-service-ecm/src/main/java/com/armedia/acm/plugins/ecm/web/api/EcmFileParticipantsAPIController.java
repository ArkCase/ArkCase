package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bojan.milenkoski on 06.10.2017
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm/participants", "/api/latest/service/ecm/participants" })
public class EcmFileParticipantsAPIController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private EcmFileParticipantService fileParticipantService;

    @PreAuthorize("hasPermission(#objectId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/FILE/{objectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public List<AcmParticipant> saveFileParticipants(
            @PathVariable(value = "objectId") Long objectId, @RequestBody List<AcmParticipant> participants, Authentication authentication)
            throws AcmParticipantsException
    {
        log.info("Participants will be set on object FILE:[{}]", objectId);

        return getFileParticipantService().setFileParticipants(objectId, participants);
    }

    @PreAuthorize("hasPermission(#objectId, 'FOLDER', 'write|group-write')")
    @RequestMapping(value = "/FOLDER/{objectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public List<AcmParticipant> saveFolderParticipants(
            @PathVariable(value = "objectId") Long objectId, @RequestBody List<AcmParticipant> participants, Authentication authentication)
            throws AcmParticipantsException
    {
        log.info("Participants will be set on object FOLDER:[{}]", objectId);

        return getFileParticipantService().setFolderParticipants(objectId, participants);
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
