package com.armedia.acm.plugins.onlyoffice.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines the presence or absence of the documents in the Open Recent...
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Recent
{
    /**
     * the folder where the document is stored (can be empty in case the document is stored in the root folder)
     */
    private String folder;
    /**
     * the document title that will be displayed in the Open Recent... menu option,
     */
    private String title;
    /**
     * the absolute URL to the document where it is stored
     */
    private String url;

    public String getFolder()
    {
        return folder;
    }

    public void setFolder(String folder)
    {
        this.folder = folder;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
