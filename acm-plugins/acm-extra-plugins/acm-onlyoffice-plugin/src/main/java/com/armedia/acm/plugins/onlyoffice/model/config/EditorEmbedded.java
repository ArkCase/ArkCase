package com.armedia.acm.plugins.onlyoffice.model.config;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EditorEmbedded
{
    /**
     * Defines the absolute URL to the document serving as a source file for the document embedded into the web page.
     */
    private String embedUrl;
    /**
     * Defines the absolute URL to the document which will open in full screen mode.
     */
    private String fullscreenUrl;
    /**
     * Defines the absolute URL that will allow the document to be saved onto the user personal computer.
     */
    private String saveUrl;
    /**
     * Defines the absolute URL that will allow other users to share this document.
     */
    private String shareUrl;
    /**
     * Defines the place for the embedded viewer toolbar, can be either top or bottom.
     */
    private String toolbarDocked;

    public String getEmbedUrl()
    {
        return embedUrl;
    }

    public void setEmbedUrl(String embedUrl)
    {
        this.embedUrl = embedUrl;
    }

    public String getFullscreenUrl()
    {
        return fullscreenUrl;
    }

    public void setFullscreenUrl(String fullscreenUrl)
    {
        this.fullscreenUrl = fullscreenUrl;
    }

    public String getSaveUrl()
    {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl)
    {
        this.saveUrl = saveUrl;
    }

    public String getShareUrl()
    {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    public String getToolbarDocked()
    {
        return toolbarDocked;
    }

    public void setToolbarDocked(String toolbarDocked)
    {
        this.toolbarDocked = toolbarDocked;
    }
}
