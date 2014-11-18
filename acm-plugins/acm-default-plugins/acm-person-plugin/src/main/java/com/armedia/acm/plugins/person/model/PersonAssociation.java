package com.armedia.acm.plugins.person.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.data.AcmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by armdev on 8/11/14.
 */
@Entity
@Table(name = "acm_person_assoc")
public class PersonAssociation implements Serializable, AcmEntity
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_person_assoc_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
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
    
    @Column(name = "cm_notes")
    private String notes;

    @ElementCollection
    @CollectionTable(
            name = "acm_person_assoc_tag",
            joinColumns = @JoinColumn(name = "cm_person_assoc_id", referencedColumnName = "cm_person_assoc_id")
    )
    @Column(name = "cm_tag")
    private List<String> tags = new ArrayList<>();


    public PersonAssociation copy()
    {
        return null;
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

    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created) 
    {
        this.created = created;
    }

    @Override
    public String getCreator() 
    {
        return creator;
    }

    @Override
    public void setCreator(String creator) 
    {
        this.creator = creator;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) 
    {
        this.modified = modified;
    }

    @Override
    public String getModifier() 
    {
        return modifier;
    }


    @Override
    public void setModifier(String modifier) 
    {
        this.modifier = modifier;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes) 
    {
        this.notes = notes;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }


}
