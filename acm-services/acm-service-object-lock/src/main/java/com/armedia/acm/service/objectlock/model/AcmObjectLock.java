package com.armedia.acm.service.objectlock.model;

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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by nebojsha on 21.08.2015.
 */
@XmlRootElement
@Entity
@Table(name = "acm_object_lock")
public class AcmObjectLock implements Serializable, AcmEntity {
    private static final long serialVersionUID = 4579477797364149888L;

    public AcmObjectLock(long objectId, String objectType) {
        this.objectId = objectId;
        this.objectType = objectType;
    }

    public AcmObjectLock() {
    }

    @Id
    @TableGenerator(name = "acm_object_lock_gen",
            table = "acm_object_lock_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_object_lock",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_object_lock_gen")
    @Column(name = "cm_object_lock_id")
    private Long id;
    
    @Column(name = "cm_object_id")
    private Long objectId;
    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_lock_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_object_lock_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_object_lock_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_object_lock_modifier")
    private String modifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @XmlTransient
    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @XmlTransient
    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @XmlTransient
    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @XmlTransient
    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
