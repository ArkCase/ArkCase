package com.armedia.acm.plugins.onlyoffice.model.config;

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
