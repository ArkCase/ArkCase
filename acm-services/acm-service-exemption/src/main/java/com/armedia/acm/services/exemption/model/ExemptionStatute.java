package com.armedia.acm.services.exemption.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_exemption_statute")
public class ExemptionStatute implements Serializable
{

    private static final long serialVersionUID = 8484945152180957284L;

    @Id
    @TableGenerator(name = "acm_exemption_statute_gen", table = "acm_exemption_statute_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_exemption_statute", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_exemption_statute_gen")
    @Column(name = "cm_exemption_statute_id")
    private Long id;

    @Column(name = "cm_parent_object_id")
    private Long parentObjectId;

    @Column(name = "cm_parent_object_type")
    private String parentObjectType;

    @Column(name = "cm_exemption_statute")
    private String exemptionStatute;

    @Column(name = "cm_exemption_status")
    private String exemptionStatus;

    @Column(name = "cm_exemption_created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_exemption_creator", nullable = false, updatable = false)
    private String creator;

    @Column(name = "cm_file_id")
    private Long fileId;

    @Column(name = "cm_file_version")
    private String fileVersion;

    @Column(name = "cm_manually_flag")
    private Boolean manuallyFlag;

    @Transient
    private List<String> exemptionStatutes;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public String getParentObjectType()
    {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    public String getExemptionStatute()
    {
        return exemptionStatute;
    }

    public void setExemptionStatute(String exemptionStatute)
    {
        this.exemptionStatute = exemptionStatute;
    }

    public String getExemptionStatus()
    {
        return exemptionStatus;
    }

    public void setExemptionStatus(String exemptionStatus)
    {
        this.exemptionStatus = exemptionStatus;
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

    public Long getFileId()
    {
        return fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public String getFileVersion()
    {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion)
    {
        this.fileVersion = fileVersion;
    }

    public Boolean getManuallyFlag()
    {
        return manuallyFlag;
    }

    public void setManuallyFlag(Boolean manuallyFlag)
    {
        this.manuallyFlag = manuallyFlag;
    }

    public List<String> getExemptionStatutes()
    {
        return exemptionStatutes;
    }

    public void setExemptionStatutes(List<String> exemptionStatutes)
    {
        this.exemptionStatutes = exemptionStatutes;
    }
}
