package com.armedia.acm.plugins.report.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by dwu on 6/9/2017.
 * <p>
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <repositoryFileDto>
 * <aclNode>false</aclNode>
 * <createdDate>1496440801215</createdDate>
 * <creatorId>9b7d2d8d-59ed-453e-9345-1b1a874a6222</creatorId>
 * <fileSize>30720</fileSize>
 * <folder>false</folder>
 * <hidden>false</hidden>
 * <id>f749c8b0-c795-46fa-9b1e-c93a184a58e2</id>
 * <lastModifiedDate>1496440801353</lastModifiedDate>
 * <locale>en</locale>
 * <locked>false</locked>
 * <name>MasterList-Scheduled.xls</name>
 * <ownerType>-1</ownerType>
 * <path>/public/generated-reports/MasterList-Scheduled.xls</path>
 * <title>MasterList-Scheduled.xls</title>
 * <versionId>1.3</versionId>
 * <versioned>true</versioned>
 * </repositoryFileDto>
 */
public class FileProperties
{

    private String aclNode;

    //use string for now
    private String createdDate;
    private String creatorId;
    private String fileSize;
    private String folder;
    private String hidden;
    private String id;
    private String lastModifiedDate;
    private String locale;
    
    //should be boolean
    private String locked;
    private String name;
    private String ownerType;
    private String path;
    private String title;
    private String versionId;
    private String versioned;

    public String getAclNode()
    {
        return aclNode;
    }

    @XmlElement
    public void setAclNode(String aclNode)
    {
        this.aclNode = aclNode;
    }

    public String getCreatedDate()
    {
        return createdDate;
    }

    @XmlElement
    public void setCreatedDate(String createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getCreatorId()
    {
        return creatorId;
    }

    @XmlElement
    public void setCreatorId(String creatorId)
    {
        this.creatorId = creatorId;
    }

    public String getFileSize()
    {
        return fileSize;
    }

    @XmlElement
    public void setFileSize(String fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getFolder()
    {
        return folder;
    }

    @XmlElement
    public void setFolder(String folder)
    {
        this.folder = folder;
    }

    public String getHidden()
    {
        return hidden;
    }

    @XmlElement
    public void setHidden(String hidden)
    {
        this.hidden = hidden;
    }

    public String getId()
    {
        return id;
    }

    @XmlElement
    public void setId(String id)
    {
        this.id = id;
    }

    public String getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    @XmlElement
    public void setLastModifiedDate(String lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLocale()
    {
        return locale;
    }

    @XmlElement
    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public String getLocked()
    {
        return locked;
    }

    @XmlElement
    public void setLocked(String locked)
    {
        this.locked = locked;
    }

    public String getName()
    {
        return name;
    }

    @XmlElement
    public void setName(String name)
    {
        this.name = name;
    }

    public String getOwnerType()
    {
        return ownerType;
    }

    @XmlElement
    public void setOwnerType(String ownerType)
    {
        this.ownerType = ownerType;
    }

    public String getPath()
    {
        return path;
    }

    @XmlElement
    public void setPath(String path)
    {
        this.path = path;
    }

    public String getTitle()
    {
        return title;
    }

    @XmlElement
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getVersionId()
    {
        return versionId;
    }

    @XmlElement
    public void setVersionId(String versionId)
    {
        this.versionId = versionId;
    }

    public String getVersioned()
    {
        return versioned;
    }

    @XmlElement
    public void setVersioned(String versioned)
    {
        this.versioned = versioned;
    }

    @Override
    public String toString()
    {
        return "FileProperties{" +
                "aclNode='" + aclNode + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", folder='" + folder + '\'' +
                ", hidden='" + hidden + '\'' +
                ", id='" + id + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", locale='" + locale + '\'' +
                ", locked='" + locked + '\'' +
                ", name='" + name + '\'' +
                ", ownerType='" + ownerType + '\'' +
                ", path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", versionId='" + versionId + '\'' +
                ", versioned='" + versioned + '\'' +
                '}';
    }
}

