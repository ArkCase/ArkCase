package com.armedia.acm.services.suggestion.model;

/*-
 * #%L
 * acm-service-case-suggestion
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

import com.armedia.acm.services.suggestion.util.JsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

public class SuggestedCase
{
    public static class File {

        private String fileId;

        private String fileName;

        public String getFileId()
        {
            return fileId;
        }

        public void setFileId(String fileId)
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
    }

    public Long caseId;

    public String caseNumber;

    public String caseTitle;

    public String caseDescription;

    public String caseStatus;

    public String objectType;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date modifiedDate;

    private File file;

    public Long getCaseId()
    {
        return caseId;
    }

    public void setCaseId(Long caseId)
    {
        this.caseId = caseId;
    }

    public String getCaseNumber()
    {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
    }

    public String getCaseTitle()
    {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle)
    {
        this.caseTitle = caseTitle;
    }

    public String getCaseDescription()
    {
        return caseDescription;
    }

    public void setCaseDescription(String caseDescription)
    {
        this.caseDescription = caseDescription;
    }

    public String getCaseStatus()
    {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus)
    {
        this.caseStatus = caseStatus;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Date getModifiedDate()
    {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }
}
