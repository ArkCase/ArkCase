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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@XmlRootElement
@Entity
@Table(name = "acm_person_contact")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.person.model.PersonContact")
public class PersonContact implements Serializable, AcmEntity
{
    private static final long serialVersionUID = 7413755227864370548L;

    @Id
    @TableGenerator(name = "acm_person_contact_gen",
            table = "acm_person_contact_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_person_contact",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_person_contact_gen")
    @Column(name = "cm_person_contact_id")
    private Long id;

    @Column(name = "cm_person_contact_attention")
    private String attention;

    @Column(name = "cm_person_contact_company_name")
    private String companyName;

    @Column(name = "cm_person_contact_name")
    private String personName;

    @Column(name = "cm_person_contact_first_name")
    private String firstName;

    @Column(name = "cm_person_contact_middle_inits")
    private String middleInitials;

    @Column(name = "cm_person_contact_last_name")
    private String lastName;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "acm_person_cntct_ident",
            joinColumns = { @JoinColumn(name="cm_person_contact_id", referencedColumnName = "cm_person_contact_id") },
            inverseJoinColumns = { @JoinColumn(name = "cm_identification_id", referencedColumnName = "cm_identification_id", unique = true)
            }
    )
    private List<Identification> identifications = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "acm_person_cntct_postal_address",
            joinColumns = { @JoinColumn(name="cm_person_contact_id", referencedColumnName = "cm_person_contact_id") },
            inverseJoinColumns = { @JoinColumn(name = "cm_address_id", referencedColumnName = "cm_address_id") }
    )
    private List<PostalAddress> addresses = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "acm_person_cntct_cntct_method",
            joinColumns = { @JoinColumn(name="cm_person_contact_id", referencedColumnName = "cm_person_contact_id") },
            inverseJoinColumns = { @JoinColumn(name = "cm_contact_method_id", referencedColumnName = "cm_contact_method_id") }
    )
    private List<ContactMethod> contactMethods = new ArrayList<>();


    @Column(name = "cm_person_contact_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_person_contact_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_person_contact_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_person_contact_modifier")
    private String modifier;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public List<Identification> getIdentifications() {
        return identifications;
    }

    public void setIdentifications(List<Identification> identifications) {
        this.identifications = identifications;
    }

    public List<PostalAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PostalAddress> addresses) {
        this.addresses = addresses;
    }

    public List<ContactMethod> getContactMethods() {
        return contactMethods;
    }

    public void setContactMethods(List<ContactMethod> contactMethods) {
        this.contactMethods = contactMethods;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getMiddleInitials()
    {
        return middleInitials;
    }

    public void setMiddleInitials(String middleInitials)
    {
        this.middleInitials = middleInitials;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
}
