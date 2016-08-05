package com.armedia.acm.services.participants.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantEventPublisher;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by marjan.stefanoski on 01.04.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/participant","/api/latest/service/participant"})
public class ListAllParticipantsByObjectTypeAndObjectId {

    private AcmParticipantService acmParticipantService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmParticipant> listParticipants(
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            Authentication authentication
    ) throws AcmObjectNotFoundException {
        if (log.isInfoEnabled()){
            log.info("List all participants on object ['" + objectType + "]:[" + objectId + "]");
        }
        List<AcmParticipant>  participants = getAcmParticipantService().listAllParticipantsPerObjectTypeAndId(objectType,objectId);
        return participants;
    }

    public AcmParticipantService getAcmParticipantService() {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService) {
        this.acmParticipantService = acmParticipantService;
    }
}

