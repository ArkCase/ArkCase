package com.armedia.acm.plugins.person.model;

import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by armdev on 7/28/14.
 */
@Entity
@Table(name = "acm_person_alias")
public class PersonAlias implements Serializable, AcmEntity
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_person_alias_gen",
            table = "acm_person_alias_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_person_alias",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_person_alias_gen")
    @Column(name = "cm_person_alias_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cm_person_id", nullable = false) 
    private Person person;

    @Column(name = "cm_person_alias_type")
    private String aliasType;
    
    @Transient
    private List<String> aliasTypes;

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

    @XmlTransient
    public Long getId()
    {
        return id;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    @XmlTransient
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @XmlTransient
    public String getAliasType() {
        return aliasType;
    }

    public void setAliasType(String aliasType) {
        this.aliasType = aliasType;
    }
    
    @XmlTransient
	public List<String> getAliasTypes() {
		return aliasTypes;
	}

	public void setAliasTypes(List<String> aliasTypes) {
		this.aliasTypes = aliasTypes;
	}

	@XmlTransient
	public String getAliasValue() 
    {
        return aliasValue;
    }

    public void setAliasValue(String aliasValue) 
    {
        this.aliasValue = aliasValue;
    }

    @XmlTransient
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

    @XmlTransient
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

    @XmlTransient
    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) 
    {
        this.modified = modified;
    }

    @XmlTransient
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

    public PersonAlias returnBase() {
    	return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonAlias that = (PersonAlias) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(aliasType, that.aliasType) &&
                Objects.equals(aliasValue, that.aliasValue);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, aliasType, aliasValue);
    }
}