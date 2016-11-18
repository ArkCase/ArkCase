package com.armedia.acm.service.objecthistory.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by teng.wang on 11/16/2016.
 */
@Entity
@Table(name = "acm_owninggroup")
public class AcmOwningGroup implements Serializable, AcmObject, AcmEntity
{

    private static final long serialVersionUID = -2617180502019871082L;

    @Id
    @TableGenerator(name = "acm_owninggroup_gen",
            table = "acm_owninggroup_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_owninggroup",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_owninggroup_gen")
    @Column(name = "cm_owninggroup_id")
    private Long id;

    @Column(name = "cm_owninggroup_object_id", insertable = true, updatable = false)
    private Long objectId;

    @Column(name = "cm_owninggroup_object_type", insertable = true, updatable = false)
    private String objectType;

    @Column(name = "cm_owninggroup_new_group")
    private String newGroup;

    @Column(name = "cm_owninggroup_old_group")
    private String oldGroup;

    @Column(name = "cm_owninggroup_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_owninggroup_creator")
    private String creator;

    @Column(name = "cm_owninggroup_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_owninggroup_modifier")
    private String modifier;


    @Override
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

    @Override
    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public String getOldGroup()
    {
        return oldGroup;
    }

    public void setOldGroup(String oldGroup)
    {
        this.oldGroup = oldGroup;
    }

    public String getNewGroup()
    {
        return newGroup;
    }

    public void setNewGroup(String newGroup)
    {
        this.newGroup = newGroup;
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
