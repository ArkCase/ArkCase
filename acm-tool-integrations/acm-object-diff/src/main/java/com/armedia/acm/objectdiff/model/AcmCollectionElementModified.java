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

import java.util.List;

public class AcmCollectionElementModified extends AcmCollectionElementChange implements AcmChangeContainer
{
    private AcmObjectModified acmObjectModified;

    public AcmCollectionElementModified(AcmObjectModified acmObjectModified)
    {
        this.acmObjectModified = acmObjectModified;
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_MODIFIED);
        setAffectedObjectId(acmObjectModified.getAffectedObjectId());
        setAffectedObjectType(acmObjectModified.getAffectedObjectType());
        setPath(acmObjectModified.getPath());
    }

    public AcmObjectModified getAcmObjectModified()
    {
        return acmObjectModified;
    }

    @Override
    public List<AcmChange> getChanges()
    {
        return acmObjectModified.getChanges();
    }

    public void addChange(AcmPropertyChange change)
    {
        this.acmObjectModified.addChange(change);
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }
}
