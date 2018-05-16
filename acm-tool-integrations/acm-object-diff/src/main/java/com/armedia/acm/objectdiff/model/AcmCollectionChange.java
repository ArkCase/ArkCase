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

public class AcmCollectionChange extends AcmPropertyChange implements AcmChangeContainer
{
    private List<AcmChange> changes = new LinkedList<>();

    public AcmCollectionChange(String path, String property)
    {
        setProperty(property);
        setPath(path);
        setAction(AcmDiffConstants.COLLECTION_CHANGED);
    }

    public List<AcmChange> getChanges()
    {
        return changes;
    }

    public void addChange(AcmCollectionElementChange elementChange)
    {
        changes.add(elementChange);
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }
}
