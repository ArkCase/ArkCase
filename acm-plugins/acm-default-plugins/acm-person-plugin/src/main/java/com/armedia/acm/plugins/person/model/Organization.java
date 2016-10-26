package com.armedia.acm.plugins.person.model;

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
import java.util.Objects;


/**
 * Created by armdev on 09/03/14.
 */
@Entity
@Table(name = "acm_organization")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = Organization.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.person.model.Organization")
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
            inverseJoinColumns = {@JoinColumn(name = "cm_identification_id", referencedColumnName = "cm_identification_id", unique = true)
            }
    )
    private List<Identification> identifications = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "acm_organization_postal_address",
            joinColumns = {@JoinColumn(name = "cm_organization_id", referencedColumnName = "cm_organization_id")},
            inverseJoinColumns = {@JoinColumn(name = "cm_address_id", referencedColumnName = "cm_address_id")}
    )
    private List<PostalAddress> addresses = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "acm_organization_contact_method",
            joinColumns = {@JoinColumn(name = "cm_organization_id", referencedColumnName = "cm_organization_id")},
            inverseJoinColumns = {@JoinColumn(name = "cm_contact_method_id", referencedColumnName = "cm_contact_method_id")}
    )
    private List<ContactMethod> contactMethods = new ArrayList<>();

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

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

}