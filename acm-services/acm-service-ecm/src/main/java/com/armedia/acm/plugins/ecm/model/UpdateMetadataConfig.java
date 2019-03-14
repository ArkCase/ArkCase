package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class UpdateMetadataConfig
{
    @JsonProperty("alfresco.metadata.update.file")
    @Value("${alfresco.metadata.update.file:''}")
    private String metadataUpdateFile;

    @JsonProperty("alfresco.metadata.update.folder")
    @Value("${alfresco.metadata.update.folder:''}")
    private String metadataUpdateFolder;

    public String getMetadataUpdateFile()
    {
        return metadataUpdateFile;
    }

    public void setMetadataUpdateFile(String metadataUpdateFile)
    {
        this.metadataUpdateFile = metadataUpdateFile;
    }

    public String getMetadataUpdateFolder()
    {
        return metadataUpdateFolder;
    }

    public void setMetadataUpdateFolder(String metadataUpdateFolder)
    {
        this.metadataUpdateFolder = metadataUpdateFolder;
    }
}
