package com.armedia.acm.tool.mediaengine.model;

/*-
 * #%L
 * acm-media-engine
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

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class MediaEngineDTO
{
    private Long id;

    private String remoteId;

    private String type;

    private String language;

    private String status;

    private String processId;

    private String creator;

    private Date created;

    private String modifier;

    private Date modified;

    private File mediaEcmFileVersion;

    private Map<String, String> properties;

    private String tempPath;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getRemoteId()
    {
        return remoteId;
    }

    public void setRemoteId(String remoteId)
    {
        this.remoteId = remoteId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId(String processId)
    {
        this.processId = processId;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public File getMediaEcmFileVersion()
    {
        return mediaEcmFileVersion;
    }

    public void setMediaEcmFileVersion(File mediaEcmFileVersion)
    {
        this.mediaEcmFileVersion = mediaEcmFileVersion;
    }

    public String getTempPath()
    {
        return tempPath;
    }

    public void setTempPath(String tempPath)
    {
        this.tempPath = tempPath;
    }
}
