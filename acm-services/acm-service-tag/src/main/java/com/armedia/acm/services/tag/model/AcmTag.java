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
@Table(name = "acm_tag")
public class AcmTag implements AcmEntity, Serializable, AcmObject {

    public static final String OBJECT_TYPE = "TAG";
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_tag_text")
    private String tagText;

    // no cm_tag_type column yet
//    @Column(name = "cm_tag_type")
//    private String type;

    @Column(name = "cm_tag_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_tag_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_tag_modifier", nullable = false, insertable = true, updatable = false)
    private String modifier;

    @Column(name = "cm_tag_modified", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_parent_object_id")
    private Long parentId;

    @Column(name = "cm_parent_object_type")
    private String parentType;

    @Column(name ="cm_file_id")
    private Long file;

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

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

    public Long getFile() {
        return file;
    }

    public void setFile(Long file) {
        this.file = file;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
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
