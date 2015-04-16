package com.armedia.acm.activiti.model;

import com.armedia.acm.data.AcmEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by nebojsha on 09.04.2015.
 */
@Entity
@Table(name = "acm_process_definition")
public class AcmProcessDefinition implements AcmEntity {
    @Id
    @Column(name = "cm_pd_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_pd_description")
    private String description;
    @Column(name = "cm_pd_name")
    private String name;
    @Column(name = "cm_pd_version")
    private int version;
    @Column(name = "cm_pd_key")
    private String key;
    @Column(name = "cm_pd_deployment_id")
    private String deploymentId;
    @Column(name = "cm_pd_file_name")
    private String fileName;
    @Column(name = "cm_pd_is_active")
    private boolean active;

    @Column(name = "cm_pd_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "cm_pd_creator", insertable = true, updatable = false)
    private String creator;
    @Column(name = "cm_pd_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "cm_pd_modifier", insertable = true, updatable = true)
    private String modifier;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
