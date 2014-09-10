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
 * Created by armdev on 8/11/14.
 */
@Entity
@Table(name = "acm_person_assoc")
public class PersonAssociation implements Serializable
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_person_assoc_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne( fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="cm_person_assoc_person_id", nullable = false) 
    private Person person;
    
    @Column(name = "cm_person_assoc_parent_id")
    private Long parentId;

    @Column(name = "cm_person_assoc_person_type")
    private String personType;
    
    @Column(name = "cm_person_assoc_parent_type")
    private String parentType;
    
    @Column(name = "cm_person_assoc_person_desc")
    private String personDescription;

    @Column(name = "cm_person_assoc_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_person_assoc_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_person_assoc_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_person_assoc_modifier")
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

    public Person getPerson()
    {
        if ( person == null )
        {
            person = new Person();
        }
        return person;
    }

    public void setPerson(Person person) 
    {
        this.person = person;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public String getPersonType() 
    {
        return personType;
    }

    public void setPersonType(String personType) 
    {
        this.personType = personType;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType) 
    {
        this.parentType = parentType;
    }

    public String getPersonDescription() 
    {
        return personDescription;
    }

    public void setPersonDescription(String personDescription) 
    {
        this.personDescription = personDescription;
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
        
        if ( getPerson() != null )
        {
            getPerson().setCreator(creator);
        }
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
        
        if ( getPerson() != null )
        {
            getPerson().setModifier(modifier);
        }
    }

    
    }   
