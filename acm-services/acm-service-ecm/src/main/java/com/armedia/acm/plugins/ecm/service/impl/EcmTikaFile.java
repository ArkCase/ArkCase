package com.armedia.acm.plugins.ecm.service.impl;

import java.util.Date;

public class EcmTikaFile
{
    private String contentType;
    private String nameExtension;
    private String gpsIso6709;
    private Double gpsLatitudeDegrees;
    private Double gpsLongitudeDegrees;
    private String gpsReadable;
    private Date created;
    private Integer heightPixels;
    private Integer widthPixels;
    private String cameraMake;
    private String cameraModel;
    private Double videoDurationSeconds;

    public Double getVideoDurationSeconds()
    {
        return videoDurationSeconds;
    }

    public void setVideoDurationSeconds(Double videoDurationSeconds)
    {
        this.videoDurationSeconds = videoDurationSeconds;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getNameExtension()
    {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension)
    {
        this.nameExtension = nameExtension;
    }

    public String getGpsIso6709()
    {
        return gpsIso6709;
    }

    public void setGpsIso6709(String gpsIso6709)
    {
        this.gpsIso6709 = gpsIso6709;
    }

    public Double getGpsLatitudeDegrees()
    {
        return gpsLatitudeDegrees;
    }

    public void setGpsLatitudeDegrees(Double gpsLatitudeDegrees)
    {
        this.gpsLatitudeDegrees = gpsLatitudeDegrees;
    }

    public Double getGpsLongitudeDegrees()
    {
        return gpsLongitudeDegrees;
    }

    public void setGpsLongitudeDegrees(Double gpsLongitudeDegrees)
    {
        this.gpsLongitudeDegrees = gpsLongitudeDegrees;
    }

    public String getGpsReadable()
    {
        return gpsReadable;
    }

    public void setGpsReadable(String gpsReadable)
    {
        this.gpsReadable = gpsReadable;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Integer getHeightPixels()
    {
        return heightPixels;
    }

    public void setHeightPixels(Integer heightPixels)
    {
        this.heightPixels = heightPixels;
    }

    public Integer getWidthPixels()
    {
        return widthPixels;
    }

    public void setWidthPixels(Integer widthPixels)
    {
        this.widthPixels = widthPixels;
    }

    public String getCameraMake()
    {
        return cameraMake;
    }

    public void setCameraMake(String cameraMake)
    {
        this.cameraMake = cameraMake;
    }

    public String getCameraModel()
    {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel)
    {
        this.cameraModel = cameraModel;
    }

    @Override
    public String toString()
    {
        return "EcmTikaFile{" +
                "contentType='" + contentType + '\'' +
                ", nameExtension='" + nameExtension + '\'' +
                ", gpsIso6709='" + gpsIso6709 + '\'' +
                ", gpsLatitudeDegrees=" + gpsLatitudeDegrees +
                ", gpsLongitudeDegrees=" + gpsLongitudeDegrees +
                ", gpsReadable='" + gpsReadable + '\'' +
                ", created=" + created +
                ", heightPixels=" + heightPixels +
                ", widthPixels=" + widthPixels +
                ", cameraMake='" + cameraMake + '\'' +
                ", cameraModel='" + cameraModel + '\'' +
                ", videoDurationSeconds=" + videoDurationSeconds +
                '}';
    }
}
