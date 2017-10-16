package com.armedia.acm.services.tag.model;

import java.io.Serializable;
import java.util.Date;

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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Entity
@Table(name = "acm_associated_tag")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmAssociatedTag implements AcmEntity, Serializable, AcmObject
{

    public static final String OBJECT_TYPE = "ASSOCIATED_TAG";
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_associated_tag_gen", table = "acm_associated_tag_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_associated_tag", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_associated_tag_gen")
    @Column(name = "cm_associated_tag_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cm_tag_id")
    private AcmTag tag;

    @Column(name = "cm_associated_tag_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_associated_tag_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_parent_object_id")
    private Long parentId;

    @Column(name = "cm_parent_object_type")
    private String parentType;

    @Column(name = "cm_parent_object_title")
    private String parentTitle;

    @Column(name = "cm_associated_tag_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_associated_tag_modifier")
    private String modifier;

    public void setId(Long id)
    {
        this.id = id;
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
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

    public AcmTag getTag()
    {
        return tag;
    }

    public Long getTagId()
    {
        return tag.getId();
    }

    public void setTag(AcmTag tag)
    {
        this.tag = tag;
    }

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

    @JsonIgnore
    @Override
    public Long getId()
    {
        return id;
    }

    public String getParentTitle() {return parentTitle; }

    public void setParentTitle(String parentTitle)
    {
        this.parentTitle = parentTitle;
    }
}
