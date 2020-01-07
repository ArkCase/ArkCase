package com.armedia.acm.core;

/*-
 * #%L
 * ACM Core API
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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class AcmApplication implements Serializable
{
    private static final long serialVersionUID = -4533090175042467646L;
    private List<AcmUserAction> topbarActions;
    private List<AcmUserAction> navigatorTabs;
    private List<AcmObjectType> objectTypes;

    private List<AcmObjectType> businessObjects;

    public List<AcmUserAction> getTopbarActions()
    {
        return Collections.unmodifiableList(topbarActions);
    }

    public void setTopbarActions(List<AcmUserAction> topbarActions)
    {
        this.topbarActions = topbarActions;
    }

    public List<AcmUserAction> getNavigatorTabs()
    {
        return Collections.unmodifiableList(navigatorTabs);
    }

    public void setNavigatorTabs(List<AcmUserAction> navigatorTabs)
    {
        this.navigatorTabs = navigatorTabs;
    }

    public List<AcmObjectType> getObjectTypes()
    {
        return objectTypes;
    }

    public void setObjectTypes(List<AcmObjectType> objectTypes)
    {
        this.objectTypes = objectTypes;
    }

    public List<AcmObjectType> getBusinessObjects()
    {
        return businessObjects;
    }

    public void setBusinessObjects(List<AcmObjectType> businessObjects)
    {
        this.businessObjects = businessObjects;
    }

    public AcmObjectType getBusinessObjectByName(String name)
    {
        for (AcmObjectType objectType : getBusinessObjects())
        {
            if (objectType.getName().equalsIgnoreCase(name))
            {
                return objectType;
            }
        }

        throw new IllegalArgumentException("No such business object with name '" + name + "'");
    }
}
