package com.armedia.acm.plugins.ecm.model;


import java.util.Date;

public class FileUpload
{
    private String name;
    private long size;
    private String url;
    private String thumbnailUrl;
    private String deleteUrl;
    private String deleteType = "DELETE";
    private String error;
    private String uploadFileType;
    private String creator;
    private Date created;
    private String status;
    private long id;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDeleteUrl()
    {
        return deleteUrl;
    }

    public void setDeleteUrl(String deleteUrl)
    {
        this.deleteUrl = deleteUrl;
    }

    public String getDeleteType()
    {
        return deleteType;
    }

    public void setDeleteType(String deleteType)
    {
        this.deleteType = deleteType;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getUploadFileType() {
        return uploadFileType;
    }

    public void setUploadFileType(String uploadFileType) {
        this.uploadFileType = uploadFileType;
    }


}
