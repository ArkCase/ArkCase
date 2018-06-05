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
public class GoBack
{
    /**
     * open the website in the new browser tab/window (if the value is set to true) or the current tab (if the value is
     * set to false) when the Go to Documents button is clicked. The default value is true
     */
    private boolean blank;
    /**
     * the text which will be displayed for the Go to Documents menu button and upper right corner button (i.e. instead
     * of Go to Documents)
     */
    private String text;
    /**
     * the absolute URL to the website address which will be opened when clicking the Go to Documents menu button
     */
    private String url;

    public boolean isBlank()
    {
        return blank;
    }

    public void setBlank(boolean blank)
    {
        this.blank = blank;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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
