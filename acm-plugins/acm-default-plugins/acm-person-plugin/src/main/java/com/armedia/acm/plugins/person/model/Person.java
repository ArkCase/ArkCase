package com.armedia.acm.plugins.person.model;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 4/7/14.
 */
@Entity
@Table(name = "acm_person")
public class Person implements Serializable
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_person_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_person_title")
    private String title;

    @Column(name = "cm_person_company_name")
    private String company;

    @Column(name = "cm_person_status")
    private String status;

    @Column(name = "cm_given_name")
    private String givenName;

    @Column(name = "cm_family_name")
    private String familyName;

    @Column(name = "cm_person_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_person_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_person_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_person_modifier")
    private String modifier;

    @ManyToMany
    @JoinTable(
            name = "acm_person_postal_address",
            joinColumns = { @JoinColumn(name="cm_person_id", referencedColumnName = "cm_person_id") },
            inverseJoinColumns = { @JoinColumn(name = "cm_address_id", referencedColumnName = "cm_address_id") }
    )
    private List<PostalAddress> addresses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "acm_person_contact_method",
            joinColumns = { @JoinColumn(name="cm_person_id", referencedColumnName = "cm_person_id") },
            inverseJoinColumns = { @JoinColumn(name = "cm_contact_method_id", referencedColumnName = "cm_contact_method_id") }
    )
    private List<ContactMethod> contactMethods = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "acm_person_security_tag",
            joinColumns = @JoinColumn(name = "cm_person_id" )
    )
    private List<String> securityTags = new ArrayList<>();

    @PrePersist
    protected void beforeInsert()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("In beforeInsert()");
        }

        if ( getStatus() == null || getStatus().trim().isEmpty() )
        {
            setStatus("ACTIVE");
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
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
        for ( PostalAddress address : getAddresses() )
        {
            if ( address.getCreator() == null )
            {
                address.setCreator(creator);
            }
        }

        for ( ContactMethod contactMethod : getContactMethods() )
        {
            if ( contactMethod.getCreator() == null )
            {
                contactMethod.setCreator(creator);
            }
        }
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
        log.info("setting person modifier to: '" + modifier + "'");

        this.modifier = modifier;

        for ( PostalAddress address : getAddresses() )
        {
            address.setModifier(modifier);
        }

        for ( ContactMethod contactMethod : getContactMethods() )
        {
            contactMethod.setModifier(modifier);
        }
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public List<PostalAddress> getAddresses()
    {
        return addresses;
    }

    public void setAddresses(List<PostalAddress> addresses)
    {
        this.addresses = addresses;
    }

    public List<ContactMethod> getContactMethods()
    {
        return contactMethods;
    }

    public void setContactMethods(List<ContactMethod> contactMethods)
    {
        this.contactMethods = contactMethods;
    }

    public List<String> getSecurityTags()
    {
        return securityTags;
    }

    public void setSecurityTags(List<String> securityTags)
    {
        this.securityTags = securityTags;
    }
}
