package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private transient final Logger log = LogManager.getLogger(getClass());
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
