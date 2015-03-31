package com.armedia.acm.services.tag.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    @Column(name = "cm_tag_description")
    private String tagDescription;

    @Column(name = "cm_tag_name")
    private String tagName;

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

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
    private List<AcmAssociatedTag> associatedTags;

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
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

    public List<AcmAssociatedTag> getAssociatedTags() {
        return associatedTags;
    }

    public void setAssociatedTags(List<AcmAssociatedTag> associatedTags) {
        this.associatedTags = associatedTags;
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

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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
