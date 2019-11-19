package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

/**
 * Created by marjan.stefanoski on 15.05.2015.
 */
public class FileDTO
{
    private String originalId;
    private EcmFile newFile;
    private Boolean link;

    public String getOriginalId()
    {
        return originalId;
    }

    public void setOriginalId(String originalId)
    {
        this.originalId = originalId;
    }

    public EcmFile getNewFile()
    {
        return newFile;
    }

    public void setNewFile(EcmFile newFile)
    {
        this.newFile = newFile;
    }

    public Boolean getLink()
    {
        return link;
    }

    public void setLink(Boolean link)
    {
        this.link = link;
    }
}
