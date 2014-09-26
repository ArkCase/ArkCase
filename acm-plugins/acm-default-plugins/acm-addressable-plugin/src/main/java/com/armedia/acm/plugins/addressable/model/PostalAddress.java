package com.armedia.acm.plugins.addressable.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "acm_postal_address")
public class PostalAddress implements Serializable
{

    private static final long serialVersionUID = 673622283387112922L;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @XmlElements({
		@XmlElement(name="created"),
		@XmlElement(name="initiatorLocationDate"),
		@XmlElement(name="peopleLocationDate")
		
	})
    @Column(name = "cm_address_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @XmlElements({
		@XmlElement(name="creator"),
		@XmlElement(name="initiatorLocationAddedBy"),
		@XmlElement(name="peopleLocationAddedBy")
		
	})
    @Column(name = "cm_address_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_address_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_address_modifier")
    private String modifier;

    @Column(name = "cm_address_status")
    private String status;

    @XmlElements({
		@XmlElement(name="type"),
		@XmlElement(name="initiatorLocationType"),
		@XmlElement(name="peopleLocationType")
		
	})
    @Column(name = "cm_address_type")
    private String type;
    
    @Transient
    private List<String> types;

    @XmlElements({
		@XmlElement(name="streetAddress"),
		@XmlElement(name="initiatorLocationAddress"),
		@XmlElement(name="peopleLocationAddress")
		
	})
    @Column(name = "cm_street_address")
    private String streetAddress;

    @Column(name = "cm_street_address_extra")
    private String streetAddress2;

    @XmlElements({
		@XmlElement(name="city"),
		@XmlElement(name="initiatorLocationCity"),
		@XmlElement(name="peopleLocationCity")
		
	})
    @Column(name = "cm_locality")
    private String city;

    @XmlElements({
		@XmlElement(name="state"),
		@XmlElement(name="initiatorLocationState"),
		@XmlElement(name="peopleLocationState")
		
	})
    @Column(name = "cm_region")
    private String state;

    @XmlElements({
		@XmlElement(name="zip"),
		@XmlElement(name="initiatorLocationZip"),
		@XmlElement(name="peopleLocationZip")
		
	})
    @Column(name = "cm_postal_code")
    private String zip;

    @Column(name = "cm_country")
    private String country;

    @PrePersist
    protected void beforeInsert()
    {
        log.info("In before insert on PostalAddress");
        if ( getStatus() == null || getStatus().trim().isEmpty() )
        {
            setStatus("ACTIVE");
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

    public Date getModified()
    {
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public String getStreetAddress()
    {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    public String getStreetAddress2()
    {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2)
    {
        this.streetAddress2 = streetAddress2;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }
}
