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

import com.armedia.acm.plugins.ecm.model.ChangedParticipant;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileParticipantChangedEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class FileParticipantChangedEventListener implements ApplicationListener<EcmFileParticipantChangedEvent>
{

    private static final Logger log = LogManager.getLogger(FileParticipantChangedEventListener.class);

    private ChangedParticipantToJmsSender changedParticipantToJmsSender;

    @Override
    public void onApplicationEvent(EcmFileParticipantChangedEvent event)
    {
        AcmParticipant changeParticipant = event.getChangedParticipant();

        if (changeParticipant != null)
        {
            EcmFile file = (EcmFile) event.getSource();

            if (changeParticipant.getParticipantLdapId() != null && changeParticipant.getParticipantType() != null
                    && changeParticipant.getObjectType() != null && file.getVersionSeriesId() != null)
            {
                // send jms messages with the file id, the changed participant information and the type of change
                ChangedParticipant changedParticipant = new ChangedParticipant();
                changedParticipant.setCmisObjectId(file.getVersionSeriesId());
                changedParticipant.setChangedParticipant(changeParticipant);
                changedParticipant.setChangeType(event.getChangeType());

                if (event.getOldParticipant() != null)
                {
                    changedParticipant.setOldParticipant(event.getOldParticipant());
                }

                getChangedParticipantToJmsSender().sendChangedParticipant(changedParticipant);
            }
        }
    }

    public ChangedParticipantToJmsSender getChangedParticipantToJmsSender()
    {
        return changedParticipantToJmsSender;
    }

    public void setChangedParticipantToJmsSender(ChangedParticipantToJmsSender changedParticipantToJmsSender)
    {
        this.changedParticipantToJmsSender = changedParticipantToJmsSender;
    }
}