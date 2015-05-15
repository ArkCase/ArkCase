package com.armedia.acm.services.participants.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;
import com.armedia.acm.services.participants.service.AcmParticipantEventPublisher;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 01.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/participant","/api/latest/service/participant"})
public class ChangeParticipantRoleAPIController {

    private AcmParticipantService acmParticipantService;
    private AcmParticipantEventPublisher acmParticipantEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{participantId}/{participantType}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmParticipant changeParticipantRole(
            @PathVariable(value = "participantId") Long participantId,
            @PathVariable(value = "participantType") String participantType,
            Authentication authentication
    ) throws AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Participant with participantId: " + participantId + " will change his Role to: " + participantType);
        }

        AcmParticipant participantForUpdate = getAcmParticipantService().findParticipant(participantId);
        AcmParticipant updatedParticipant;
        try {
            updatedParticipant = getAcmParticipantService().changeParticipantRole(participantForUpdate,participantType);
            if (log.isInfoEnabled()) {
                log.info("Participant with participantId: " + participantId + " successfully changed his Role to: " + participantType);
            }
            getAcmParticipantEventPublisher().publishParticipantUpdatedEvent(updatedParticipant, authentication, true);
        } catch ( Exception e ) {
            if( log.isErrorEnabled() )
                log.error("Exception occurred while changing participant's role on participantId: "+participantId,e);
            getAcmParticipantEventPublisher().publishParticipantUpdatedEvent(participantForUpdate, authentication, false);
            throw new AcmUserActionFailedException(ParticipantConstants.USER_ACTION_UPDATE,ParticipantConstants.OBJECT_TYPE,participantId,"Updating the Role on the Participant failed!",e);
        }
        return updatedParticipant;
    }

    public AcmParticipantService getAcmParticipantService() {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService) {
        this.acmParticipantService = acmParticipantService;
    }

    public AcmParticipantEventPublisher getAcmParticipantEventPublisher() {
        return acmParticipantEventPublisher;
    }

    public void setAcmParticipantEventPublisher(AcmParticipantEventPublisher acmParticipantEventPublisher) {
        this.acmParticipantEventPublisher = acmParticipantEventPublisher;
    }
}
