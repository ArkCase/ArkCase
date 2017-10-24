package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantService;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by bojan.milenkoski on 06.10.2017
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm/participants", "/api/latest/service/ecm/participants" })
public class EcmFileParticipantsAPIController
{
    private AcmFolderService folderService;
    private AcmParticipantService participantService;
    private EcmFileParticipantService fileParticipantService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasPermission(#objectId, #objectType, 'write')")
    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmParticipant> saveParticipants(@PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId, @RequestBody List<AcmParticipant> participants, Authentication authentication)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmParticipantsException, AcmAccessControlException
    {
        log.info("Participants will be set on object [{}]:[{}]", objectType, objectId);

        if (!objectType.equals(EcmFileConstants.OBJECT_FOLDER_TYPE) && !objectType.equals(EcmFileConstants.OBJECT_FILE_TYPE))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The called method cannot be executed on objectType {" + objectType + "}!");
        }
        getParticipantService().validateParticipants(participants);

        List<AcmParticipant> participantsToReturn = new ArrayList<>();

        List<AcmParticipant> existingParticipants = getParticipantService().listAllParticipantsPerObjectTypeAndId(objectType, objectId);

        // remove deleted participants
        for (AcmParticipant existingParticipant : existingParticipants)
        {
            if (participants.stream()
                    .filter(participant -> participant.getParticipantLdapId().equals(existingParticipant.getParticipantLdapId())
                            && participant.getParticipantType().equals(existingParticipant.getParticipantType()))
                    .count() == 0)
            {
                getParticipantService().removeParticipant(existingParticipant.getParticipantLdapId(),
                        existingParticipant.getParticipantType(), existingParticipant.getObjectType(), existingParticipant.getObjectId());

                if (objectType.equals(EcmFileConstants.OBJECT_FOLDER_TYPE))
                {
                    getFileParticipantService().removeParticipantFromFolderAndChildren(getFolderService().findById(objectId),
                            existingParticipant.getParticipantLdapId(), existingParticipant.getObjectType());
                }
            }
        }

        // change existing participants role
        for (AcmParticipant participant : participants)
        {
            Optional<AcmParticipant> returnedParticipant = existingParticipants.stream()
                    .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                    .findFirst();

            if (!returnedParticipant.isPresent())
            {
                continue;
            }

            AcmParticipant changedParticipant = getParticipantService().changeParticipantRole(returnedParticipant.get(),
                    returnedParticipant.get().getParticipantType());
            participantsToReturn.add(changedParticipant);

            if (objectType.equals(EcmFileConstants.OBJECT_FOLDER_TYPE) && (participant.isReplaceChildrenParticipant()))
            {
                getFileParticipantService().setParticipantToFolderChildren(getFolderService().findById(objectId), participant);
            }
        }

        // add new participants
        for (AcmParticipant participant : participants)
        {
            if (existingParticipants.stream()
                    .filter(existingParticipant -> existingParticipant.getParticipantLdapId().equals(participant.getParticipantLdapId()))
                    .count() == 0)
            {
                AcmParticipant addedParticipant = getParticipantService().saveParticipant(participant.getParticipantLdapId(),
                        participant.getParticipantType(), objectId, objectType);
                participantsToReturn.add(addedParticipant);

                if (objectType.equals(EcmFileConstants.OBJECT_FOLDER_TYPE) && (participant.isReplaceChildrenParticipant()))
                {
                    getFileParticipantService().setParticipantToFolderChildren(getFolderService().findById(objectId), participant);
                }
            }
        }

        return participantsToReturn;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public AcmParticipantService getParticipantService()
    {
        return participantService;
    }

    public void setParticipantService(AcmParticipantService acmParticipantService)
    {
        this.participantService = acmParticipantService;
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
