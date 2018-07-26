package com.armedia.acm.services.wopi.model;

/*-
 * #%L
 * ACM Service: Wopi service
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

public class WopiFileInfo
{
    private Long fileId;

    private String name;

    private String extension;

    private String owner;

    private String version;

    private Long size;

    public WopiFileInfo(Long fileId, String name, String extension, String owner, String version, Long size)
    {
        this.fileId = fileId;
        this.name = String.format("%s.%s", name, extension);
        this.extension = extension;
        this.owner = owner;
        this.version = version;
        this.size = size;
    }

    public String getFileId()
    {
        return fileId.toString();
    }

    public String getName()
    {
        return name;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getOwner()
    {
        return owner;
    }

    public String getVersion()
    {
        return version;
    }

    public Long getSize()
    {
        return size;
    }
}
