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
import com.armedia.acm.objectonverter.ObjectConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AcmObjectDiff extends AcmDiff
{
    private AcmObjectChange acmObjectChange;

    public AcmObjectDiff(AcmObjectChange acmObjectChange, ObjectConverter objectConverter)
    {
        super(objectConverter);
        this.acmObjectChange = acmObjectChange;
    }

    @Override
    public AcmChange getChangesAsTree()
    {
        return acmObjectChange;
    }

    @Override
    public List<AcmChange> getChangesAsList()
    {
        if (acmObjectChange instanceof AcmObjectModified)
        {
            return getChangesForChangeContainer((AcmObjectModified) acmObjectChange);
        }
        else
        {
            ArrayList<AcmChange> acmChanges = new ArrayList<>();
            if (acmObjectChange != null)
            {
                acmChanges.add(acmObjectChange);
            }
            return acmChanges;
        }
    }

    private List<AcmChange> getChangesForChangeContainer(AcmChangeContainer acmChangeContainer)
    {
        List<AcmChange> changes = new LinkedList<>();
        for (AcmChange change : acmChangeContainer.getChanges())
        {
            if (change.isLeaf())
            {
                changes.add(change);
            }
            else if (change instanceof AcmChangeContainer)
            {
                changes.addAll(getChangesForChangeContainer((AcmChangeContainer) change));
            }
        }
        return changes;
    }
}
