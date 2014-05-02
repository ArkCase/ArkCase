package com.armedia.acm.plugins.alfrescorma.model;

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
