package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.service.objectlock.model.AcmObjectLock;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 3/12/15.
 */
public class AcmCmisObject implements Serializable
{
    private static final long serialVersionUID = -8094324419148482666L;

    private Long objectId;
    private String objectType;
    private String name;
    private String version;
    private List<EcmFileVersion> versionList;
    private Date created;
    private String creator;
    private Date modified;
    private String modifier;
    private String type;
    private String category;
    private String cmisObjectId;
    private String mimeType;
    private String status;
    private Integer pageCount;
    private AcmObjectLock lock;
    private String ext;
    private boolean publicFlag;
    private String reviewStatus;
    private String redactionStatus;
    private boolean link;

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCmisObjectId()
    {
        return cmisObjectId;
    }

    public void setCmisObjectId(String cmisObjectId)
    {
        this.cmisObjectId = cmisObjectId;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public List<EcmFileVersion> getVersionList()
    {
        return versionList;
    }

    public void setVersionList(List<EcmFileVersion> versionList)
    {
        this.versionList = versionList;
    }

    public Integer getPageCount()
    {
        return pageCount;
    }

    public void setPageCount(Integer pageCount)
    {
        this.pageCount = pageCount;
    }

    public AcmObjectLock getLock()
    {
        return lock;
    }

    public void setLock(AcmObjectLock lock)
    {
        this.lock = lock;
    }

    public String getExt()
    {
        return ext;
    }

    public void setExt(String ext)
    {
        this.ext = ext;
    }

    public boolean isLink()
    {
        return link;
    }

    public void setLink(boolean link)
    {
        this.link = link;
    }

    public boolean getPublicFlag()
    {
        return publicFlag;
    }

    public void setPublicFlag(boolean publicFlag)
    {
        this.publicFlag = publicFlag;
    }

    public String getReviewStatus()
    {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus)
    {
        this.reviewStatus = reviewStatus;
    }

    public String getRedactionStatus()
    {
        return redactionStatus;
    }

    public void setRedactionStatus(String redactionStatus)
    {
        this.redactionStatus = redactionStatus;
    }
}
