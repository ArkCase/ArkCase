package com.armedia.acm.services.email.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.util.List;

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EmailWithAttachmentsAndLinksDTO extends EmailWithEmbeddedLinksDTO implements AttachmentsProcessableDTO
{
    private List<Long> attachmentIds;

    private List<String> filePaths;

    @Override
    public List<Long> getAttachmentIds()
    {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Long> attachmentIds)
    {
        this.attachmentIds = attachmentIds;
    }

    @Override
    public List<String> getFilePaths()
    {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths)
    {
        this.filePaths = filePaths;
    }

}
