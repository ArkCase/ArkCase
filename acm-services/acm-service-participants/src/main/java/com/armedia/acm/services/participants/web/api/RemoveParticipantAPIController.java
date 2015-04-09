package com.armedia.acm.services.participants.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;
import com.armedia.acm.services.participants.service.AcmParticipantEventPublisher;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import org.activiti.engine.impl.util.json.JSONObject;
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
public class RemoveParticipantAPIController {

    private AcmParticipantService acmParticipantService;
    private AcmParticipantEventPublisher acmParticipantEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{userId}/{participantType}/{objectType}/{objectId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeParticipant(
            @PathVariable(value = "userId") String userId,
            @PathVariable(value = "participantType") String participantType,
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            Authentication authentication
    ) throws AcmUserActionFailedException {
        if ( log.isInfoEnabled() ) {
            log.info("Participant "+userId+" with participant type:" + participantType +" will be removed from object['" + objectType + "]:[" + objectId + "]");
        }
        AcmParticipant participant = getAcmParticipantService().getParticipantByParticipantTypeAndObjectTypeAndId(userId,participantType,objectType,objectId);
        try {
            if (participant != null) {
                getAcmParticipantService().removeParticipant(participant);
                if (log.isDebugEnabled())
                    log.debug("Participant" + userId + "  successfully removed from object['" + objectType + "]:[" + objectId + "]");
                getAcmParticipantEventPublisher().publishParticipantDeletedEvent(participant, authentication, true);
                return prepareJsonReturnMsg(ParticipantConstants.SUCCESS_DELETE_MSG, userId, objectType, objectId);
            } else {
                if (log.isDebugEnabled())
                    log.debug("Association not found between Participant" + userId + " and object['" + objectType + "]:[" + objectId + "] in  the DB");
                getAcmParticipantEventPublisher().publishParticipantDeletedEvent(participant, authentication, false);
                return prepareJsonReturnMsg(ParticipantConstants.SUCCESS_DELETE_MSG,userId, objectType, objectId);
            }
        } catch ( Exception e ) {
            if (log.isErrorEnabled())
                log.error("SQL Exception was thrown while removing participant "+ userId+" from object['" + objectType + "]:[" + objectId + "]");
            throw new AcmUserActionFailedException(ParticipantConstants.USER_ACTION_DELETE, ParticipantConstants.OBJECT_TYPE,participant.getId(),"SQL Exception was thrown while deleting Participant",e);
        }

    }

    private String prepareJsonReturnMsg(String msg, String userId, String objectType, Long objectId) {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedParticipant", userId);
        objectToReturnJSON.put("fromObjectType", objectType);
        objectToReturnJSON.put("andObjectId",objectId);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
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
