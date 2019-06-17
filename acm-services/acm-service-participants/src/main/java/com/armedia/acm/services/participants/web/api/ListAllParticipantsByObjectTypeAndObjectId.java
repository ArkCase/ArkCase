package com.armedia.acm.services.participants.web.api;

/*-
 * #%L
 * ACM Service: Participants
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
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;
import com.armedia.acm.services.participants.service.AcmParticipantService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.FlushModeType;

import java.util.List;

/**
 * Created by marjan.stefanoski on 01.04.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/participant", "/api/latest/service/participant" })
public class ListAllParticipantsByObjectTypeAndObjectId
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmParticipantService acmParticipantService;

    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants(objectTypeIndex = 0, objectIdIndex = 1)
    @ResponseBody
    public List<AcmParticipant> listParticipants(
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            Authentication authentication) throws AcmObjectNotFoundException
    {
        if (log.isInfoEnabled())
        {
            log.info("List all participants on object ['" + objectType + "]:[" + objectId + "]");
        }
        List<AcmParticipant> participants = getAcmParticipantService().listAllParticipantsPerObjectTypeAndId(objectType, objectId,
                FlushModeType.COMMIT);

        return participants;
    }

    public AcmParticipantService getAcmParticipantService()
    {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService)
    {
        this.acmParticipantService = acmParticipantService;
    }
}
