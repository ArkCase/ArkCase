package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by armdev on 10/21/14.
 */
public class AcmObjectChangelist
{
    private List<Object> addedObjects = new CopyOnWriteArrayList<>();
    private List<Object> updatedObjects = new CopyOnWriteArrayList<>();
    private List<Object> deletedObjects = new CopyOnWriteArrayList<>();

    public List<Object> getAddedObjects()
    {
        return addedObjects;
    }

    public void setAddedObjects(List<Object> addedObjects)
    {
        this.addedObjects = addedObjects;
    }

    public List<Object> getUpdatedObjects()
    {
        return updatedObjects;
    }

    public void setUpdatedObjects(List<Object> updatedObjects)
    {
        this.updatedObjects = updatedObjects;
    }

    public List<Object> getDeletedObjects()
    {
        return deletedObjects;
    }

    public void setDeletedObjects(List<Object> deletedObjects)
    {
        this.deletedObjects = deletedObjects;
    }

    @Override
    public String toString()
    {
        return "AcmObjectChangelist{" +
                "addedObjects=" + addedObjects +
                ", updatedObjects=" + updatedObjects +
                ", deletedObjects=" + deletedObjects +
                '}';
    }
}
