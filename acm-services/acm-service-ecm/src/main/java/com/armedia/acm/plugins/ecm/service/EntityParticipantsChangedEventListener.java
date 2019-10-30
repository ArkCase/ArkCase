package com.armedia.acm.plugins.ecm.service;

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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.dataaccess.model.AcmEntityParticipantsChangedEvent;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

public class EntityParticipantsChangedEventListener implements ApplicationListener<AcmEntityParticipantsChangedEvent>
{

    private final transient Logger log = LogManager.getLogger(getClass());

    private EcmFileParticipantService fileParticipantService;

    @Override
    public void onApplicationEvent(AcmEntityParticipantsChangedEvent event)
    {
        AcmObject obj = (AcmObject) event.getSource();
        List<AcmParticipant> originalParticipants = event.getOriginalParticipants();
        // create copy of the participants, because DataAccessPrivilegeListener.handleParticipantsChanged changes the
        // inherit children participant flag
        List<AcmParticipant> newParticipants = copyParticipants(((AcmAssignedObject) obj).getParticipants());

        if (obj instanceof AcmAssignedObject && obj instanceof AcmContainerEntity)
        {
            // inherit participants
            if (obj.getId() == null)
            {
                newParticipants.forEach(participant -> participant.setReplaceChildrenParticipant(true));
            }

            log.debug("Inheriting file participants from " + obj.getObjectType() + "[" + obj.getId() + "]");
            getFileParticipantService().inheritParticipantsFromAssignedObject(
                    newParticipants,
                    originalParticipants,
                    ((AcmContainerEntity) obj).getContainer(), ((AcmAssignedObject) obj).getRestricted());
        }
    }

    private List<AcmParticipant> copyParticipants(List<AcmParticipant> participants)
    {
        List<AcmParticipant> copiedParticipants = new ArrayList<>();
        participants.forEach(p -> copiedParticipants.add(AcmParticipant.createRulesTestParticipant(p)));
        return copiedParticipants;
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
