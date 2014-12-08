package com.armedia.acm.service.milestone.model;

import com.armedia.acm.data.AcmEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_milestone")
public class AcmMilestone implements Serializable, AcmEntity
{
    private static final long serialVersionUID = -2866319464429863768L;

    @Id
    @Column(name = "cm_milestone_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_milestone_object_id")
    private Long objectId;

    @Column(name = "cm_milestone_object_type")
    private String objectType;

    @Column(name = "cm_milestone_achieved_date")
    @Temporal(TemporalType.DATE)
    private Date milestoneDate;

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

    public Date getMilestoneDate()
    {
        return milestoneDate;
    }

    public void setMilestoneDate(Date milestoneDate)
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
}
