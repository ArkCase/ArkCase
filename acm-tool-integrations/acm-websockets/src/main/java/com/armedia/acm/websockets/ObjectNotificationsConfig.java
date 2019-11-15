package com.armedia.acm.websockets;

/*-
 * #%L
 * Tool Integrations: ArkCase Web Sockets
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

import com.armedia.acm.configuration.annotations.ListValue;

import java.util.List;

public class ObjectNotificationsConfig
{
    private List<String> includedObjectTypes;

    private List<String> includedClassNames;

    private List<String> includedParentObjectTypes;

    private List<String> includedRootParentObjectTypes;

    @ListValue(value = "acm.object.changed.notification.filter.include.object_types")
    public List<String> getIncludedObjectTypes()
    {
        return includedObjectTypes;
    }

    public void setIncludedObjectTypes(List<String> includedObjectTypes)
    {
        this.includedObjectTypes = includedObjectTypes;
    }

    @ListValue(value = "acm.object.changed.notification.filter.include.classNames")
    public List<String> getIncludedClassNames()
    {
        return includedClassNames;
    }

    public void setIncludedClassNames(List<String> includedClassNames)
    {
        this.includedClassNames = includedClassNames;
    }

    @ListValue(value = "acm.object.changed.notification.filter.include.parent_object_types")
    public List<String> getIncludedParentObjectTypes()
    {
        return includedParentObjectTypes;
    }

    public void setIncludedParentObjectTypes(List<String> includedParentObjectTypes)
    {
        this.includedParentObjectTypes = includedParentObjectTypes;
    }

    @ListValue(value = "acm.object.changed.notification.filter.include.root_parent_object_types")
    public List<String> getIncludedRootParentObjectTypes()
    {
        return includedRootParentObjectTypes;
    }

    public void setIncludedRootParentObjectTypes(List<String> includedRootParentObjectTypes)
    {
        this.includedRootParentObjectTypes = includedRootParentObjectTypes;
    }
}
