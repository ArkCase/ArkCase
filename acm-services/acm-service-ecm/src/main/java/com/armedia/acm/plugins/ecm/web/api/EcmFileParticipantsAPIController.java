package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.participants.model.AcmParticipant;

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

import java.util.Arrays;
import java.util.List;

/**
 * Created by bojan.milenkoski on 06.10.2017
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm/participants", "/api/latest/service/ecm/participants" })
public class EcmFileParticipantsAPIController
{
    private EcmFileParticipantService fileParticipantService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasPermission(#objectId, #objectType, 'write|group-write')")
    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmParticipant> saveParticipants(@PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId, @RequestBody List<AcmParticipant> participants, Authentication authentication)
            throws AcmParticipantsException, AcmAccessControlException
    {
        log.info("Participants will be set on object [{}]:[{}]", objectType, objectId);

        if (!objectType.equals(EcmFileConstants.OBJECT_FOLDER_TYPE) && !objectType.equals(EcmFileConstants.OBJECT_FILE_TYPE))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The called method cannot be executed on objectType {" + objectType + "}!");
        }

        return getFileParticipantService().setFileParticipants(objectId, objectType, participants);
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
