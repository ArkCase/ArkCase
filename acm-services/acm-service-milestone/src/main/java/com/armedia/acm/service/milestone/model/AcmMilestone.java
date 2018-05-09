package com.armedia.acm.service.milestone.model;

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.converter.LocalDateConverter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "acm_milestone")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmMilestone implements Serializable, AcmEntity
{
    private static final long serialVersionUID = -2866319464429863768L;

    @Id
    @TableGenerator(name = "acm_milestone_gen", table = "acm_milestone_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_milestone", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_milestone_gen")
    @Column(name = "cm_milestone_id")
    private Long id;

    @Column(name = "cm_milestone_object_id", insertable = true, updatable = false)
    private Long objectId;

    @Column(name = "cm_milestone_object_type", insertable = true, updatable = false)
    private String objectType;

    @Column(name = "cm_milestone_achieved_date")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate milestoneDate;

    @Column(name = "cm_milestone_name")
    private String milestoneName;

    @Column(name = "cm_milestone_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_milestone_creator")
    private String creator;

    @Column(name = "cm_milestone_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_milestone_modifier")
    private String modifier;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

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

    public LocalDate getMilestoneDate()
    {
        return milestoneDate;
    }

    public void setMilestoneDate(LocalDate milestoneDate)
    {
        this.milestoneDate = milestoneDate;
    }

    public String getMilestoneName()
    {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName)
    {
        this.milestoneName = milestoneName;
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
    public boolean equals(Object obj)
    {
        Objects.requireNonNull(obj, "Comparable object must not be null");
        if (!(obj instanceof AcmMilestone))
            return false;
        AcmMilestone other = (AcmMilestone) obj;
        if (this.getId() == null || other.getId() == null)
            return false;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode()
    {
        if (getId() == null)
            return super.hashCode();
        else
            return getId().hashCode();
    }
}
