package com.armedia.acm.plugins.person.model;

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by armdev on 09/03/14.
 */
@Entity
@Table(name = "acm_organization")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = Organization.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.person.model.Organization")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Organization implements Serializable, AcmEntity
{
    private static final long serialVersionUID = 7413755227864370548L;

    @Id
    @TableGenerator(name = "acm_organization_gen",
            table = "acm_organization_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_organization",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_organization_gen")
    @Column(name = "cm_organization_id")
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "acm_organization_identification",
            joinColumns = {@JoinColumn(name = "cm_organization_id", referencedColumnName = "cm_organization_id")},
            inverseJoinColumns = {@JoinColumn(name = "cm_identification_id", referencedColumnName = "cm_identification_id", unique = true)})
    private List<Identification> identifications = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "acm_organization_postal_address",
            joinColumns = {@JoinColumn(name = "cm_organization_id", referencedColumnName = "cm_organization_id")},
            inverseJoinColumns = {@JoinColumn(name = "cm_address_id", referencedColumnName = "cm_address_id")})
    private List<PostalAddress> addresses = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "acm_organization_contact_method",
            joinColumns = {@JoinColumn(name = "cm_organization_id", referencedColumnName = "cm_organization_id")},
            inverseJoinColumns = {@JoinColumn(name = "cm_contact_method_id", referencedColumnName = "cm_contact_method_id")})
    private List<ContactMethod> contactMethods = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumns({
            @JoinColumn(name = "cm_parent_id", referencedColumnName = "cm_organization_id"),
            @JoinColumn(name = "cm_parent_type", referencedColumnName = "cm_object_type")})
    @OrderBy("created ASC")
    private List<OrganizationAssociation> associationsToObjects = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true, mappedBy = "organization")
    private List<OrganizationAssociation> associationsFromObjects = new ArrayList<>();

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    /**
     * ContactMethod which is default as phone
     */
    @OneToOne
    @JoinColumn(name = "cm_default_phone")
    private ContactMethod defaultPhone;

    /**
     * ContactMethod which is default as email
     */
    @OneToOne
    @JoinColumn(name = "cm_default_email")
    private ContactMethod defaultEmail;

    /**
     * PostalAddress which is default
     */
    @OneToOne
    @JoinColumn(name = "cm_default_address")
    private PostalAddress defaultAddress;

    /**
     * ContactMethod which is default as url
     */
    @OneToOne
    @JoinColumn(name = "cm_default_url")
    private ContactMethod defaultUrl;

    /**
     * ContactMethod which is default as fax
     */
    @OneToOne
    @JoinColumn(name = "cm_default_fax")
    private ContactMethod defaultFax;

    /**
     * Identification which is default from identifications
     */
    @OneToOne
    @JoinColumn(name = "cm_default_identification")
    private Identification defaultIdentification;

    @Lob
    @Column(name = "cm_details")
    private String details;

    @Column(name = "cm_object_type", updatable = false)
    private String objectType = PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "organization")
    private List<OrganizationDBA> organizationDBAs = new ArrayList<>();

    /**
     * OrganizationDBA which is default as dba
     */
    @OneToOne
    @JoinColumn(name = "cm_default_dba")
    private OrganizationDBA defaultDBA;

    /**
     * OrganizationAssociation which is primary contact with parentType == PERSON
     */
    @OneToOne
    @JoinColumn(name = "cm_primary_contact")
    private PersonOrganizationAssociation primaryContact;

    /**
     * Parent Organization
     */
    @OneToOne
    @JoinColumn(name = "cm_parent_organization")
    private Organization parentOrganization;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization", orphanRemoval = true)
    List<PersonOrganizationAssociation> personAssociations = new ArrayList<>();

    @PostLoad
    protected void postLoad()
    {
        /*FIXME this code is added because in personAliases or personAssosiation additional sql is executed to fetch
           same person which is parent to them.
            Because of deadline didn't have time to find better solution for this like: get object from cache or explore new possibilities
            So when we optimize JPA not to fetch same entity (with same ID) more than once in same transaction, this code should be removed
            linked with technical dept: AFDP-3487
            */
        updateChildObjectsWithParentObjectReference();
    }


    @PrePersist
    protected void beforeInsert()
    {
        updateChildObjectsWithParentObjectReference();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        updateChildObjectsWithParentObjectReference();
    }


    /**
     * Updates child objects which (should) have reference to **this** object instance, reference is lost due to (de)serialization from/to JSON, XMl, etc.
     * Also JPA without caching returns different instance for same object, executes additional query for same object id.
     */
    private void updateChildObjectsWithParentObjectReference()
    {
        for (OrganizationAssociation pa : getAssociationsFromObjects())
        {
            pa.setOrganization(this);
        }

        for (OrganizationDBA dba : getOrganizationDBAs())
        {
            dba.setOrganization(this);
        }
        for (PersonOrganizationAssociation poa : getPersonAssociations())
        {
            poa.setOrganization(this);
        }
    }

    @XmlTransient
    public Long getOrganizationId()
    {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId)
    {
        this.organizationId = organizationId;
    }

    @XmlTransient
    public String getOrganizationType()
    {
        return organizationType;
    }

    public void setOrganizationType(String organizationType)
    {
        this.organizationType = organizationType;
    }

    @XmlTransient
    public List<String> getOrganizationTypes()
    {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<String> organizationTypes)
    {
        this.organizationTypes = organizationTypes;
    }

    @XmlTransient
    public String getOrganizationValue()
    {
        return organizationValue;
    }

    public void setOrganizationValue(String organizationValue)
    {
        this.organizationValue = organizationValue;
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
    public Date getModified()
    {
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

    public Organization returnBase()
    {
        return this;
    }

    public List<Identification> getIdentifications()
    {
        return identifications;
    }

    public void setIdentifications(List<Identification> identifications)
    {
        this.identifications = identifications;
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

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public ContactMethod getDefaultPhone()
    {
        return defaultPhone;
    }

    public void setDefaultPhone(ContactMethod defaultPhone)
    {
        this.defaultPhone = defaultPhone;
    }

    public ContactMethod getDefaultEmail()
    {
        return defaultEmail;
    }

    public void setDefaultEmail(ContactMethod defaultEmail)
    {
        this.defaultEmail = defaultEmail;
    }

    public PostalAddress getDefaultAddress()
    {
        return defaultAddress;
    }

    public void setDefaultAddress(PostalAddress defaultAddress)
    {
        this.defaultAddress = defaultAddress;
    }

    public ContactMethod getDefaultUrl()
    {
        return defaultUrl;
    }

    public void setDefaultUrl(ContactMethod defaultUrl)
    {
        this.defaultUrl = defaultUrl;
    }

    public Identification getDefaultIdentification()
    {
        return defaultIdentification;
    }

    public void setDefaultIdentification(Identification defaultIdentification)
    {
        this.defaultIdentification = defaultIdentification;
    }

    public void setDefaultFax(ContactMethod defaultFax)
    {
        this.defaultFax = defaultFax;
    }

    public ContactMethod getDefaultFax()
    {
        return defaultFax;
    }

    public List<OrganizationAssociation> getAssociationsToObjects()
    {
        return associationsToObjects;
    }

    public void setAssociationsToObjects(List<OrganizationAssociation> associationsToObjects)
    {
        this.associationsToObjects = associationsToObjects;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public List<OrganizationAssociation> getAssociationsFromObjects()
    {
        return associationsFromObjects;
    }

    public void setAssociationsFromObjects(List<OrganizationAssociation> associationsFromObjects)
    {
        this.associationsFromObjects = associationsFromObjects;
    }

    public List<OrganizationDBA> getOrganizationDBAs()
    {
        return organizationDBAs;
    }

    public void setOrganizationDBAs(List<OrganizationDBA> organizationDBAs)
    {
        this.organizationDBAs = organizationDBAs;
    }

    public OrganizationDBA getDefaultDBA()
    {
        return defaultDBA;
    }

    public void setDefaultDBA(OrganizationDBA defaultDBA)
    {
        this.defaultDBA = defaultDBA;
    }

    public PersonOrganizationAssociation getPrimaryContact()
    {
        return primaryContact;
    }

    public void setPrimaryContact(PersonOrganizationAssociation primaryContact)
    {
        this.primaryContact = primaryContact;
    }

    public Organization getParentOrganization()
    {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrganization)
    {
        this.parentOrganization = parentOrganization;
    }

    public List<PersonOrganizationAssociation> getPersonAssociations()
    {
        return personAssociations;
    }

    public void setPersonAssociations(List<PersonOrganizationAssociation> personAssociations)
    {
        this.personAssociations = personAssociations;
    }
}