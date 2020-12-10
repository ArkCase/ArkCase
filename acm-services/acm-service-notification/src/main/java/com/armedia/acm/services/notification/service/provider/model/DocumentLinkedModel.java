package com.armedia.acm.services.notification.service.provider.model;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

public class DocumentLinkedModel
{
    private final List<String> links;
    private final List<String> fileNames;
    private final String objectType;
    private final String objectNumber;

    public DocumentLinkedModel(List<String> links, List<String> fileNames, String objectType, String objectNumber)
    {
        this.links = links;
        this.fileNames = fileNames;
        this.objectType = objectType;
        this.objectNumber = objectNumber;
    }

    public List<String> getLinks() 
    {
        return links;
    }

    public List<String> getFileNames() 
    {
        return fileNames;
    }

    public String getObjectType() 
    {
        return objectType;
    }

    public String getObjectNumber() 
    {
        return objectNumber;
    }
}
