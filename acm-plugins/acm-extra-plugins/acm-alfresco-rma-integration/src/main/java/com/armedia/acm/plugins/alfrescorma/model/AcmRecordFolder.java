package com.armedia.acm.plugins.alfrescorma.model;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import java.io.Serializable;

public class AcmRecordFolder implements Serializable
{
    private static final long serialVersionUID = -2838758910535448686L;
    private String folderType;
    private String folderName;
    private String categoryFolder;

    public String getFolderType()
    {
        return folderType;
    }

    public void setFolderType(String folderType)
    {
        this.folderType = folderType;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }

    public String getCategoryFolder()
    {
        return categoryFolder;
    }

    public void setCategoryFolder(String categoryFolder)
    {
        this.categoryFolder = categoryFolder;
    }
}
