package com.armedia.acm.plugins.onlyoffice.model.config;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * The document info section allows to change additional parameters for the document (document author, folder where the
 * document is stored, creation date, sharing settings).
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DocumentInfo
{
    /**
     * Defines the name of the document author/creator.
     */
    private String author;
    /**
     * Defines the document creation date.
     */
    private String created;
    /**
     * Defines the folder where the document is stored (can be empty in case the document is stored in the root folder).
     */
    private String folder;
    /**
     * Defines the settings which will allow to share the document with other users:
     */
    private List<UserPermission> sharingSettings;

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getCreated()
    {
        return created;
    }

    public void setCreated(String created)
    {
        this.created = created;
    }

    public String getFolder()
    {
        return folder;
    }

    public void setFolder(String folder)
    {
        this.folder = folder;
    }

    public List<UserPermission> getSharingSettings()
    {
        return sharingSettings;
    }

    public void setSharingSettings(List<UserPermission> sharingSettings)
    {
        this.sharingSettings = sharingSettings;
    }
}
