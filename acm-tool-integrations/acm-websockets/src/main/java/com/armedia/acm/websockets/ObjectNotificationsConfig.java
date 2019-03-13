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

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class ObjectNotificationsConfig
{
    @Value("#{'${acm.object.changed.notification.filter.include.object_types}'.split(',')}")
    private List<String> includedObjectTypes;

    @Value("#{'${acm.object.changed.notification.filter.include.classNames}'.split(',')}")
    private List<String> includedClassNames;

    @Value("#{'${acm.object.changed.notification.filter.include.parent_object_types}'.split(',')}")
    private List<String> includedParentObjectTypes;

    @Value("#{'${acm.object.changed.notification.filter.include.root_parent_object_types}'.split(',')}")
    private List<String> includedRootParentObjectTypes;

    public List<String> getIncludedObjectTypes()
    {
        return includedObjectTypes;
    }

    public void setIncludedObjectTypes(List<String> includedObjectTypes)
    {
        this.includedObjectTypes = includedObjectTypes;
    }

    public List<String> getIncludedClassNames()
    {
        return includedClassNames;
    }

    public void setIncludedClassNames(List<String> includedClassNames)
    {
        this.includedClassNames = includedClassNames;
    }

    public List<String> getIncludedParentObjectTypes()
    {
        return includedParentObjectTypes;
    }

    public void setIncludedParentObjectTypes(List<String> includedParentObjectTypes)
    {
        this.includedParentObjectTypes = includedParentObjectTypes;
    }

    public List<String> getIncludedRootParentObjectTypes()
    {
        return includedRootParentObjectTypes;
    }

    public void setIncludedRootParentObjectTypes(List<String> includedRootParentObjectTypes)
    {
        this.includedRootParentObjectTypes = includedRootParentObjectTypes;
    }
}
