package com.armedia.acm.services.participants.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
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


/**
 * Created by marjan.stefanoski on 01.04.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/participant","/api/latest/service/participant"})
public class AddParticipantAPIController {

    private AcmParticipantService acmParticipantService;
    private AcmParticipantEventPublisher acmParticipantEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{userId}/{participantType}/{objectType}/{objectId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmParticipant addParticipant(
            @PathVariable(value = "userId") String userId,
            @PathVariable(value = "participantType") String participantType,
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            Authentication authentication
    ) throws AcmCreateObjectFailedException {
        if ( log.isInfoEnabled() ) {
            log.info("Participant "+userId+" with participant type:" + participantType +" will be added on object['" + objectType + "]:[" + objectId + "]" );
        }
        AcmParticipant returnedParticipant = getAcmParticipantService().getParticipantByParticipantTypeAndObjectTypeAndId(userId,participantType,objectType,objectId);

        if(returnedParticipant !=null) {
            if (log.isDebugEnabled()) {
                log.debug("Participant: " + userId + "  already exists and is added on object['" + objectType + "]:[" + objectId + "] as a "+participantType);
            }
            return returnedParticipant;
        } else {
            AcmParticipant addedParticipant = null;
            try {
                addedParticipant = getAcmParticipantService().saveParticipant(userId, participantType, objectId, objectType);
                getAcmParticipantEventPublisher().publishParticipantCreatedEvent(addedParticipant, authentication, true);
                return addedParticipant;
            } catch ( Exception e ) {
                if ( log.isErrorEnabled() )
                    log.error("Exception occurred while trying to add the Participant "+userId+" on object['" + objectType + "]:[" + objectId + "] as a "+participantType,e);
                getAcmParticipantEventPublisher().publishParticipantCreatedEvent(addedParticipant, authentication, false);
                throw new AcmCreateObjectFailedException(ParticipantConstants.OBJECT_TYPE, "Participant "+ userId +" was not added on object['" + objectType + "]:[" + objectId + "] as a "+participantType+" and there is no row inserted into DB due to exception: ", e);
            }
        }

    }

    public AcmParticipantEventPublisher getAcmParticipantEventPublisher() {
        return acmParticipantEventPublisher;
    }

    public void setAcmParticipantEventPublisher(AcmParticipantEventPublisher acmParticipantEventPublisher) {
        this.acmParticipantEventPublisher = acmParticipantEventPublisher;
    }

    public AcmParticipantService getAcmParticipantService() {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService) {
        this.acmParticipantService = acmParticipantService;
    }
}
