package com.armedia.acm.services.notification.service.provider.model;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.core.AcmObject;

public class AcmEntityTemplateModel
{
    public AcmObject caseFileObject;
    public String assigneeUserId;
    public String modifierUserId;
    public String assigneeGroupId;
    public Boolean isTitleEnabled;

    public AcmObject getCaseFileObject()
    {
        return caseFileObject;
    }

    public void setCaseFileObject(AcmObject caseFileObject)
    {
        this.caseFileObject = caseFileObject;
    }

    public String getAssigneeUserId()
    {
        return assigneeUserId;
    }

    public void setAssigneeUserId(String assigneeUserId)
    {
        this.assigneeUserId = assigneeUserId;
    }

    public String getModifierUserId()
    {
        return modifierUserId;
    }

    public void setModifierUserId(String modifierUserId)
    {
        this.modifierUserId = modifierUserId;
    }

    public String getAssigneeGroupId()
    {
        return assigneeGroupId;
    }

    public void setAssigneeGroupId(String assigneeGroupId)
    {
        this.assigneeGroupId = assigneeGroupId;
    }

    public Boolean getTitleEnabled()
    {
        return isTitleEnabled;
    }

    public void setTitleEnabled(Boolean titleEnabled)
    {
        isTitleEnabled = titleEnabled;
    }
}

