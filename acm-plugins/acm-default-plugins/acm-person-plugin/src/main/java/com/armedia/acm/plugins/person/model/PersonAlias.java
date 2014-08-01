package com.armedia.acm.plugins.person.model;

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
 * Created by armdev on 7/28/14.
 */
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

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cm_person_id", nullable = false) 
    private Person person;

    @Column(name = "cm_person_alias_type")
    private String aliasTypeId;

    @Column(name = "cm_person_alias_value")
    private String aliasValue;

    @Column(name = "cm_person_alias_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

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


    public String getAliasTypeId() {
        return aliasTypeId;
    }

    public void setAliasTypeId(String aliasTypeId) {
        this.aliasTypeId = aliasTypeId;
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
