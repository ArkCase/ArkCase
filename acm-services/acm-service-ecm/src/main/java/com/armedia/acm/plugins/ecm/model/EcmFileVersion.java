package com.armedia.acm.plugins.ecm.model;

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

    @Column(name = "cm_media_camera_make")
    private String cameraMake;

    @Column(name = "cm_media_camera_model")
    private String cameraModel;

    @Column(name = "cm_media_vid_duration_seconds")
    private Double videoDurationSeconds;

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

    public Double getVideoDurationSeconds()
    {
        return videoDurationSeconds;
    }

    public void setVideoDurationSeconds(Double videoDurationSeconds)
    {
        this.videoDurationSeconds = videoDurationSeconds;
    }

    public Date getMediaCreated()
    {
        return mediaCreated;
    }

    public void setMediaCreated(Date mediaCreated)
    {
        this.mediaCreated = mediaCreated;
    }

    @JsonIgnore
    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
