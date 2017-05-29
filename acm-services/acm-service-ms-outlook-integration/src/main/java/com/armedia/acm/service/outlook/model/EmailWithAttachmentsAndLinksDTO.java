package com.armedia.acm.service.outlook.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.util.List;

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EmailWithAttachmentsAndLinksDTO extends EmailWithEmbeddedLinksDTO
{
    private List<Long> attachmentIds;

    private List<String> filePaths;

    public List<Long> getAttachmentIds()
    {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Long> attachmentIds)
    {
        this.attachmentIds = attachmentIds;
    }

    public List<String> getFilePaths()
    {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths)
    {
        this.filePaths = filePaths;
    }

}

