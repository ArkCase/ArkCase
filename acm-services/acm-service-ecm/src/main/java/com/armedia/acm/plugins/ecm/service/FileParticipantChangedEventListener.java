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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class FileParticipantChangedEventListener implements ApplicationListener<EcmFileParticipantChangedEvent>
{

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private SendChangedParticipantToAlfresco sendChangedParticipantToAlfresco;

    @Override
    public void onApplicationEvent(EcmFileParticipantChangedEvent event)
    {
        EcmFile file = (EcmFile) event.getSource();

        AcmParticipant changeParticipant = null;
        if (event.getChangedParticipant() != null)
        {
            changeParticipant = event.getChangedParticipant();
        }
        else if (event.getAddedNewParticipant() != null)
        {
            changeParticipant = event.getAddedNewParticipant();
        }
        else if (event.getDeletedParticipant() != null)
        {
            changeParticipant = event.getDeletedParticipant();
        }

        // send jms messages with the file/folder id and the changed participant informationN
        ChangedParticipant changedParticipant = new ChangedParticipant();
        changedParticipant.setCmisObjectId(file.getVersionSeriesId());
        changedParticipant.setChangedParticipant(changeParticipant);
        getSendChangedParticipantToAlfresco().sendChangedParticipant(changedParticipant);
    }

    public SendChangedParticipantToAlfresco getSendChangedParticipantToAlfresco()
    {
        return sendChangedParticipantToAlfresco;
    }

    public void setSendChangedParticipantToAlfresco(SendChangedParticipantToAlfresco sendChangedParticipantToAlfresco)
    {
        this.sendChangedParticipantToAlfresco = sendChangedParticipantToAlfresco;
    }
}