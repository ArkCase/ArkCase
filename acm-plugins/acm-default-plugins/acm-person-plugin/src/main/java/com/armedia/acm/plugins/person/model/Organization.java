package com.armedia.acm.plugins.person.model;

import com.armedia.acm.data.AcmEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by armdev on 09/03/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "acm_organization")
public class Organization implements Serializable, AcmEntity
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_organization_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;
   
    @XmlElements({
		@XmlElement(name="organizationType"),
		@XmlElement(name="initiatorOrganizationType"),
		@XmlElement(name="peopleOrganizationType")
		
	})
    @Column(name = "cm_organization_type")
    private String organizationType;
    
    @Transient
    private List<String> organizationTypes;

    @XmlElements({
		@XmlElement(name="name"),
		@XmlElement(name="initiatorOrganizationName"),
		@XmlElement(name="peopleOrganizationName")
		
	})
    @Column(name = "cm_organization_value")
    private String organizationValue;

    @XmlElements({
		@XmlElement(name="created"),
		@XmlElement(name="initiatorOrganizationDate"),
		@XmlElement(name="peopleOrganizationDate")
		
	})
    @Column(name = "cm_organization_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @XmlElements({
		@XmlElement(name="creator"),
		@XmlElement(name="initiatorOrganizationAddedBy"),
		@XmlElement(name="peopleOrganizationAddedBy")
		
	})
    @Column(name = "cm_organization_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_organization_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_organization_modifier")
    private String modifier;

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
	public List<String> getOrganizationTypes() {
		return organizationTypes;
	}
	
	public void setOrganizationTypes(List<String> organizationTypes) {
		this.organizationTypes = organizationTypes;
	}

	public String getOrganizationValue() {
        return organizationValue;
    }

    public void setOrganizationValue(String organizationValue) {
        this.organizationValue = organizationValue;
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
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
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
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    
    
    }   
