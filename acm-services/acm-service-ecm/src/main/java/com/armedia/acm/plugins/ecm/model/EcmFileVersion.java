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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_file_version")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EcmFileVersion implements AcmEntity, Serializable, AcmObject
{
    private static final String OBJECT_TYPE = "FILE_VERSION";
    private static final long serialVersionUID = 1281659634956850724L;

    @Id
    @TableGenerator(name = "acm_file_version_gen", table = "acm_file_version_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_file_version", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_file_version_gen")
    @Column(name = "cm_file_version_id")
    private Long id;

    @Column(name = "cm_file_version_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_file_version_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_file_version_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_file_version_modifier")
    private String modifier;

    @Column(name = "cm_cmis_object_id")
    private String cmisObjectId;

    @Column(name = "cm_file_version_version_tag")
    private String versionTag;

    @Column(name = "cm_file_version_mime_type")
    private String versionMimeType;

    @Column(name = "cm_file_version_name_extension")
    private String versionFileNameExtension;

    @Column(name = "cm_media_gps_iso_6709")
    private String gpsIso6709;

    @Column(name = "cm_media_gps_latitude_degrees")
    private Double gpsLatitudeDegrees;

    @Column(name = "cm_media_gps_longitude_degrees")
    private Double gpsLongitudeDegrees;

    @Column(name = "cm_media_gps_readable")
    private String gpsReadable;

    @Column(name = "cm_media_created_date")
    private Date mediaCreated;

    @Column(name = "cm_media_height_pixels")
    private Integer heightPixels;

    @Column(name = "cm_media_width_pixels")
    private Integer widthPixels;

    @Column(name = "cm_media_device_make")
    private String deviceMake;

    @Column(name = "cm_media_device_model")
    private String deviceModel;

    @Column(name = "cm_media_duration_seconds")
    private Double durationSeconds;

    @Column(name = "cm_file_size_bytes")
    private Long fileSizeBytes;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cm_file_id")
    private EcmFile file;

    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    @Override
    public String getCreator()
    {
        return creator;
    }

    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    @Override
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    @Override
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCmisObjectId()
    {
        return cmisObjectId;
    }

    public void setCmisObjectId(String cmisObjectId)
    {
        this.cmisObjectId = cmisObjectId;
    }

    public String getVersionTag()
    {
        return versionTag;
    }

    public void setVersionTag(String versionTag)
    {
        this.versionTag = versionTag;
    }

    public String getVersionMimeType()
    {
        return versionMimeType;
    }

    public void setVersionMimeType(String versionMimeType)
    {
        this.versionMimeType = versionMimeType;
    }

    public String getVersionFileNameExtension()
    {
        return versionFileNameExtension;
    }

    public void setVersionFileNameExtension(String versionFileNameExtension)
    {
        this.versionFileNameExtension = versionFileNameExtension;
    }

    public EcmFile getFile()
    {
        return file;
    }

    public void setFile(EcmFile file)
    {
        this.file = file;
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

    public String getDeviceMake()
    {
        return deviceMake;
    }

    public void setDeviceMake(String deviceMake)
    {
        this.deviceMake = deviceMake;
    }

    public String getDeviceModel()
    {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public Double getDurationSeconds()
    {
        return durationSeconds;
    }

    public void setDurationSeconds(Double durationSeconds)
    {
        this.durationSeconds = durationSeconds;
    }

    public Date getMediaCreated()
    {
        return mediaCreated;
    }

    public void setMediaCreated(Date mediaCreated)
    {
        this.mediaCreated = mediaCreated;
    }

    public Long getFileSizeBytes()
    {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes)
    {
        this.fileSizeBytes = fileSizeBytes;
    }

    @JsonIgnore
    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
