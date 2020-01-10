package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author ivana.shekerova on 04/04/2019.
 */
public class ChangedParticipant
{
    private String cmisObjectId;
    private String changeType;
    private AcmParticipant changedParticipant;
    private AcmParticipant oldParticipant;

    public String getCmisObjectId()
    {
        return cmisObjectId;
    }

    public void setCmisObjectId(String cmisObjectId)
    {
        this.cmisObjectId = cmisObjectId;
    }

    public String getChangeType()
    {
        return changeType;
    }

    public void setChangeType(String changeType)
    {
        this.changeType = changeType;
    }

    public AcmParticipant getChangedParticipant()
    {
        return changedParticipant;
    }

    public void setChangedParticipant(AcmParticipant changedParticipant)
    {
        this.changedParticipant = changedParticipant;
    }

    public AcmParticipant getOldParticipant()
    {
        return oldParticipant;
    }

    public void setOldParticipant(AcmParticipant oldParticipant)
    {
        this.oldParticipant = oldParticipant;
    }
}
