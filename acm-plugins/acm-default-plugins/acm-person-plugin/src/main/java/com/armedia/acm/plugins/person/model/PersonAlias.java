package com.armedia.acm.plugins.person.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
 * Created by armdev on 7/28/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "acm_person_alias")
public class PersonAlias implements Serializable
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_person_alias_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cm_person_id", nullable = false) 
    private Person person;

    @XmlElements({
		@XmlElement(name="aliasType"),
		@XmlElement(name="initiatorAliasType"),
		@XmlElement(name="peopleAliasType")
		
	})
    @Column(name = "cm_person_alias_type")
    private String aliasType;
    
    @Transient
    private List<String> aliasTypes;

    @XmlElements({
		@XmlElement(name="aliasValue"),
		@XmlElement(name="initiatorAliasValue"),
		@XmlElement(name="peopleAliasValue")
		
	})
    @Column(name = "cm_person_alias_value")
    private String aliasValue;

    @XmlElements({
		@XmlElement(name="created"),
		@XmlElement(name="initiatorAliasDate"),
		@XmlElement(name="peopleAliasDate")
		
	})
    @Column(name = "cm_person_alias_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @XmlElements({
		@XmlElement(name="creator"),
		@XmlElement(name="initiatorAliasAddedBy"),
		@XmlElement(name="peopleAliasAddedBy")
		
	})
    @Column(name = "cm_person_alias_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_person_alias_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_person_alias_modifier")
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
    
    public Long getId() 
    {
        return id;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


    public String getAliasType() {
        return aliasType;
    }

    public void setAliasType(String aliasType) {
        this.aliasType = aliasType;
    }
    
	public List<String> getAliasTypes() {
		return aliasTypes;
	}

	public void setAliasTypes(List<String> aliasTypes) {
		this.aliasTypes = aliasTypes;
	}

	public String getAliasValue() 
    {
        return aliasValue;
    }

    public void setAliasValue(String aliasValue) 
    {
        this.aliasValue = aliasValue;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created) 
    {
        this.created = created;
    }

    public String getCreator() 
    {
        return creator;
    }

    public void setCreator(String creator) 
    {
        this.creator = creator;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) 
    {
        this.modified = modified;
    }

    public String getModifier() 
    {
        return modifier;
    }

    public void setModifier(String modifier) 
    {
        this.modifier = modifier;
    }

    
    }   
