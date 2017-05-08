package com.armedia.acm.plugins.addressable.model;

import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_contact_method")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ContactMethod implements Serializable, AcmEntity
{
    private static final long serialVersionUID = 1827685289454605556L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "contact_method_gen",
            table = "acm_contact_method_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_contact_method",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "contact_method_gen")
    @Column(name = "cm_contact_method_id")
    private Long id;

    @Column(name = "cm_contact_method_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_contact_method_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_contact_method_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_contact_method_modifier")
    private String modifier;

    @Column(name = "cm_contact_method_status")
    private String status;

    @Column(name = "cm_contact_type")
    private String type;

    @Column(name = "cm_contact_sub_type")
    private String subType;

    @Transient
    private List<String> types;

    @Column(name = "cm_contact_value")
    private String value;

    @Column(name = "cm_description")
    private String description;

    @PrePersist
    protected void beforeInsert()
    {
        setData();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setData();
    }

    private void setData()
    {
        if (getStatus() == null || getStatus().trim().isEmpty())
        {
            setStatus("ACTIVE");
        }
    }

    @XmlTransient
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @XmlTransient
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

    @XmlTransient
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

    @XmlTransient
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

    @XmlTransient
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

    @XmlTransient
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @XmlTransient
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlTransient
    public String getSubType()
    {
        return subType;
    }

    public void setSubType(String subType)
    {
        this.subType = subType;
    }

    @XmlTransient
    public List<String> getTypes()
    {
        return types;
    }

    public void setTypes(List<String> types)
    {
        this.types = types;
    }

    @XmlTransient
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public ContactMethod returnBase()
    {
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}
