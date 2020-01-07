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

    private Long fileId;
    private String fileName;
    private Date dateModified;
    private String fileActiveVersionNameExtension;
    private Long fileSizeBytes;
    private Long containerId;
    private String containerObjectTitle;
    private String containerObjectType;
    private Long recycleBinItemId;

    public RecycleBinItemDTO()
    {

    }

    public RecycleBinItemDTO(Long fileId, String fileName, Date dateModified,
            String fileActiveVersionNameExtension, Long fileSizeBytes,
            Long containerId, String containerObjectTitle, String containerObjectType, Long recycleBinItemId)
    {
        this.fileId = fileId;
        this.fileName = fileName;
        this.dateModified = dateModified;
        this.fileActiveVersionNameExtension = fileActiveVersionNameExtension;
        this.fileSizeBytes = fileSizeBytes;
        this.containerId = containerId;
        this.containerObjectTitle = containerObjectTitle;
        this.containerObjectType = containerObjectType;
        this.recycleBinItemId = recycleBinItemId;

    }

    public Long getContainerId()
    {
        return containerId;
    }

    public void setContainerId(Long containerId)
    {
        this.containerId = containerId;
    }

    public Long getFileId()
    {
        return fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
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

    public Long getRecycleBinItemId()
    {
        return recycleBinItemId;
    }

    public void setRecycleBinItemId(Long recycleBinItemId)
    {
        this.recycleBinItemId = recycleBinItemId;
    }
}
