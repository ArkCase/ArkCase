package com.armedia.acm.services.tag.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Entity
@Table(name = "acm_associated_tag")
public class AcmAssociatedTag implements AcmEntity, Serializable, AcmObject {

    public static final String OBJECT_TYPE = "ASSOCIATED_TAG";
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_associated_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public AcmTag getTag() {
        return tag;
    }

    public Long getTagId() {return tag.getId();}

    public void setTag(AcmTag tag) {
        this.tag = tag;
    }

    @JsonIgnore
    @Override
    public String getModifier() {
        // Not used. Modifier not exist in the database
        return null;
    }


    @Override
    public void setModifier(String modifier) {
        // Not used. Modifier not exist in the database
    }

    @JsonIgnore
    @Override
    public Date getModified() {
        // Not used. Modified not exist in the database
        return null;
    }

    @Override
    public void setModified(Date modified) {
        // Not used. Modified not exist in the database
    }

    @JsonIgnore
    @Override
    public String getObjectType() {
        return OBJECT_TYPE;
    }

    @JsonIgnore
    @Override
    public Long getId() {
        return id;
    }
}
