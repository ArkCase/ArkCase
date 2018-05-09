package com.armedia.acm.plugins.casefile.model;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseOptions
{
    private Long caseFileId;
    private List<AttachmentDTO> attachments;
    private boolean preserveFolderStructure;

    public boolean isPreserveFolderStructure()
    {
        return preserveFolderStructure;
    }

    public void setPreserveFolderStructure(boolean preserveFolderStructure)
    {
        this.preserveFolderStructure = preserveFolderStructure;
    }

    public Long getCaseFileId()
    {
        return caseFileId;
    }

    public void setCaseFileId(Long caseFileId)
    {
        this.caseFileId = caseFileId;
    }

    public List<AttachmentDTO> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments)
    {
        this.attachments = attachments;
    }

    public static class AttachmentDTO
    {
        private Long id;
        private String type;

        public AttachmentDTO(Long id, String type)
        {
            this.id = id;
            this.type = type;
        }

        public AttachmentDTO()
        {
        }

        public Long getId()
        {
            return id;
        }

        public void setId(Long id)
        {
            this.id = id;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }
    }
}
