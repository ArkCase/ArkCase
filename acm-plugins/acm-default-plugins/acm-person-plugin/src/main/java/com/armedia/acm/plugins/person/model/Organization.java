package com.armedia.acm.plugins.person.model;

import com.armedia.acm.data.AcmEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by armdev on 09/03/14.
 */
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
   
    @Column(name = "cm_organization_type")
    private String organizationType;
    
    @Transient
    private List<String> organizationTypes;

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

    @XmlTransient
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @XmlTransient
    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }
    
    @XmlTransient
	public List<String> getOrganizationTypes() {
		return organizationTypes;
	}
	
	public void setOrganizationTypes(List<String> organizationTypes) {
		this.organizationTypes = organizationTypes;
	}

	@XmlTransient
	public String getOrganizationValue() {
        return organizationValue;
    }

    public void setOrganizationValue(String organizationValue) {
        this.organizationValue = organizationValue;
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
    
    public Organization returnBase() {
    	return this;
    }
    
}