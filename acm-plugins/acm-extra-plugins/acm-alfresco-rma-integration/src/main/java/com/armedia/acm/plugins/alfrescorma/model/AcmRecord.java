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
import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmRecord implements Serializable
{
    private static final long serialVersionUID = 4185644370996265260L;
    private String categoryFolder;
    private String recordFolder;
    private Date publishedDate;
    private Date receivedDate;
    private String originator;
    private String originatorOrg;
    private String ecmFileId;

    public String getCategoryFolder()
    {
        return categoryFolder;
    }

    public void setCategoryFolder(String categoryFolder)
    {
        this.categoryFolder = categoryFolder;
    }

    public String getRecordFolder()
    {
        return recordFolder;
    }

    public void setRecordFolder(String recordFolder)
    {
        this.recordFolder = recordFolder;
    }

    public Date getPublishedDate()
    {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate)
    {
        this.publishedDate = publishedDate;
    }

    public Date getReceivedDate()
    {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate)
    {
        this.receivedDate = receivedDate;
    }

    public String getOriginator()
    {
        return originator;
    }

    public void setOriginator(String originator)
    {
        this.originator = originator;
    }

    public String getOriginatorOrg()
    {
        return originatorOrg;
    }

    public void setOriginatorOrg(String originatorOrg)
    {
        this.originatorOrg = originatorOrg;
    }

    public String getEcmFileId()
    {
        return ecmFileId;
    }

    public void setEcmFileId(String ecmFileId)
    {
        this.ecmFileId = ecmFileId;
    }
}
