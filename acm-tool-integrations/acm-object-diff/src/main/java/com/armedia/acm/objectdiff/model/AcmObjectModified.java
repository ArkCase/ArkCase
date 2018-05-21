package com.armedia.acm.objectdiff.model;

/*-
 * #%L
 * Tool Integrations: Object Diff Util
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

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeContainer;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

import java.util.LinkedList;
import java.util.List;

public class AcmObjectModified extends AcmObjectChange implements AcmChangeContainer
{
    private Long affectedObjectId;
    private String affectedObjectType;
    private List<AcmChange> changes = new LinkedList<>();

    public AcmObjectModified()
    {
        setAction(AcmDiffConstants.OBJECT_MODIFIED);
    }

    public List<AcmChange> getChanges()
    {
        return changes;
    }

    public void setChanges(List<AcmChange> changes)
    {
        this.changes = changes;
    }

    public void addChange(AcmPropertyChange change)
    {
        changes.add(change);
    }

    public Long getAffectedObjectId()
    {
        return affectedObjectId;
    }

    public void setAffectedObjectId(Long affectedObjectId)
    {
        this.affectedObjectId = affectedObjectId;
    }

    public String getAffectedObjectType()
    {
        return affectedObjectType;
    }

    public void setAffectedObjectType(String affectedObjectType)
    {
        this.affectedObjectType = affectedObjectType;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }
}
