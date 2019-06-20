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

import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderParticipantChangedEvent;
import com.armedia.acm.plugins.ecm.model.ChangedParticipant;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class FolderParticipantChangedEventListener implements ApplicationListener<AcmFolderParticipantChangedEvent>
{

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private ChangedParticipantToJmsSender changedParticipantToJmsSender;
    private String directoryName;

    @Override
    public void onApplicationEvent(AcmFolderParticipantChangedEvent event)
    {
        AcmParticipant changeParticipant = event.getChangedParticipant();

        if (changeParticipant != null)
        {
            AcmFolder folder = (AcmFolder) event.getSource();

            if (changeParticipant.getParticipantLdapId() != null && changeParticipant.getParticipantType() != null
                    && changeParticipant.getObjectType() != null && folder.getCmisFolderId() != null)
            {
                // send jms messages with the folder id, the changed participant information and the type of change
                ChangedParticipant changedParticipant = new ChangedParticipant();
                changedParticipant.setCmisObjectId(folder.getCmisFolderId());
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

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }
}