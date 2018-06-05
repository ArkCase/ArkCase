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
public class Document
{
    /**
     * Defines the type of the file for the source viewed or edited document.
     */
    private String fileType;
    /**
     * Defines the unique document identifier used for document recognition by the service. In case the known key is
     * sent the document will be taken from the cache. Every time the document is edited and saved, the key must be
     * generated anew. The document url can be used as the key but without the special characters and the length is
     * limited to 20 symbols.
     */
    private String key;
    /**
     * Defines the desired file name for the viewed or edited document which will also be used as file name when the
     * document is downloaded.
     */
    private String title;
    /**
     * Defines the absolute URL where the source viewed or edited document is stored.
     */
    private String url;
    /**
     * The document info section allows to change additional parameters for the document (document author, folder where
     * the document is stored, creation date, sharing settings).
     */
    private DocumentInfo info;
    /**
     * The document permission section allows to change the permission for the document to be edited and downloaded or
     * not.
     */
    private DocumentPermissions permissions;

    public Document(String fileType, String key, String title, String url)
    {
        this.fileType = fileType;
        this.key = key;
        this.title = title;
        this.url = url;
    }

    public void setInfo(DocumentInfo info)
    {
        this.info = info;
    }

    public void setPermissions(DocumentPermissions permissions)
    {
        this.permissions = permissions;
    }

    public String getFileType()
    {
        return fileType;
    }

    public String getKey()
    {
        return key;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }

    public DocumentInfo getInfo()
    {
        return info;
    }

    public DocumentPermissions getPermissions()
    {
        return permissions;
    }
}
