package com.armedia.acm.plugins.person.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by armdev on 09/03/14.
 */
@Entity
@Table(name = "acm_organization")
public class Organization implements Serializable
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_organization_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;
   
    @Column(name = "cm_organization_type")
    private String organizationType;

    @Column(name = "cm_organization_value")
    private String organizationValue;

    @Column(name = "cm_organization_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_organization_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_organization_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_organization_modifier")
    private String modifier;

    @PrePersist
    protected void beforeInsert()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("In beforeInsert()");
        }
        if ( getCreated() == null )
        {
            setCreated(new Date());
        }

        if ( getModified() == null )
        {
            setModified(new Date());
        }

   }

    @PreUpdate
    protected void beforeUpdate()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("In beforeUpdate()");
        }
        setModified(new Date());
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getOrganizationValue() {
        return organizationValue;
    }

    public void setOrganizationValue(String organizationValue) {
        this.organizationValue = organizationValue;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    
    
    }   
