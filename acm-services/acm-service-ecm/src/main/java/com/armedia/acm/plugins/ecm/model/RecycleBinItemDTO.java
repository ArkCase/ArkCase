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

import java.util.Date;

/**
 * @author darko.dimitrievski
 */

public class RecycleBinItemDTO
{

    private Long sourceId;
    private String sourceName;
    private String sourceType;
    private Date dateModified;
    private String fileActiveVersionNameExtension;
    private Long fileSizeBytes;
    private Long containerId;
    private String containerObjectTitle;
    private String containerObjectType;
    private Long id;

    public RecycleBinItemDTO()
    {

    }

    public RecycleBinItemDTO(Long sourceId, String sourceName, String sourceType, Date dateModified,
            String fileActiveVersionNameExtension, Long fileSizeBytes,
            Long containerId, String containerObjectTitle, String containerObjectType, Long id)
    {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.dateModified = dateModified;
        this.fileActiveVersionNameExtension = fileActiveVersionNameExtension;
        this.fileSizeBytes = fileSizeBytes;
        this.containerId = containerId;
        this.containerObjectTitle = containerObjectTitle;
        this.containerObjectType = containerObjectType;
        this.id = id;

    }

    public Long getContainerId()
    {
        return containerId;
    }

    public void setContainerId(Long containerId)
    {
        this.containerId = containerId;
    }

    public Long getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(Long sourceId)
    {
        this.sourceId = sourceId;
    }

    public String getSourceName()
    {
        return sourceName;
    }

    public String getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    public void setSourceName(String sourceName)
    {
        this.sourceName = sourceName;
    }

    public Date getDateModified()
    {
        return dateModified;
    }

    public void setDateModified(Date date)
    {
        this.dateModified = date;
    }

    public String getFileActiveVersionNameExtension()
    {
        return fileActiveVersionNameExtension;
    }

    public void setFileActiveVersionNameExtension(String fileActiveVersionNameExtension)
    {
        this.fileActiveVersionNameExtension = fileActiveVersionNameExtension;
    }

    public Long getFileSizeBytes()
    {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes)
    {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getContainerObjectType()
    {
        return containerObjectType;
    }

    public void setContainerObjectType(String containerObjectType)
    {
        this.containerObjectType = containerObjectType;
    }

    public String getContainerObjectTitle()
    {
        return containerObjectTitle;
    }

    public void setContainerObjectTitle(String containerObjectTitle)
    {
        this.containerObjectTitle = containerObjectTitle;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
}
