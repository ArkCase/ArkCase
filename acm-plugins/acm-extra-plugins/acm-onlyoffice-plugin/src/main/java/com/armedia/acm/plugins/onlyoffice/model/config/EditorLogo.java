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
public class EditorLogo
{
    /**
     * path to the image file used to show in common work mode (i.e. in view and edit modes for all editors). The image
     * must have the following size: 172x40
     */
    private String image;
    /**
     * path to the image file used to show in the embedded mode (see the config section to find out how to define the
     * embedded document type). The image must have the following size: 248x40
     */
    private String imageEmbedded;

    /**
     * the absolute URL which will be used when someone clicks the logo image (can be used to go to your web site,
     * etc.)
     */
    private String url;

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getImageEmbedded()
    {
        return imageEmbedded;
    }

    public void setImageEmbedded(String imageEmbedded)
    {
        this.imageEmbedded = imageEmbedded;
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
