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

import com.armedia.acm.objectdiff.model.interfaces.AcmChangeDisplayable;
import com.armedia.acm.objectdiff.model.interfaces.AcmDiffConstants;

public class AcmCollectionElementRemoved extends AcmCollectionElementChange implements AcmChangeDisplayable
{
    private Object affectedObject;
    private String displayOldValue;
    private String displayNewValue;

    public AcmCollectionElementRemoved(Object object)
    {
        setAction(AcmDiffConstants.COLLECTION_ELEMENT_REMOVED);
        setAffectedObject(object);
    }

    public Object getAffectedObject()
    {
        return affectedObject;
    }

    public void setAffectedObject(Object affectedObject)
    {
        this.affectedObject = affectedObject;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public String getOldValue()
    {
        return displayOldValue;
    }

    public void setOldValue(String displayOldValue)
    {
        this.displayOldValue = displayOldValue;
    }

    @Override
    public String getNewValue()
    {
        return displayNewValue;
    }

    public void setNewValue(String displayNewValue)
    {
        this.displayNewValue = displayNewValue;
    }
}
