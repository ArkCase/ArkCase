package com.armedia.acm.plugins.addressable.model;

import com.armedia.acm.data.AcmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_postal_address")
public class PostalAddress implements Serializable, AcmEntity
{

    private static final long serialVersionUID = 673622283387112922L;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "postal_address_gen",
            table = "acm_postal_address_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_postal_address",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "postal_address_gen")
    @Column(name = "cm_address_id")
    private Long id;

    @Column(name = "cm_address_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_address_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_address_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_address_modifier")
    private String modifier;

    @Column(name = "cm_address_status")
    private String status = "ACTIVE";

    @Column(name = "cm_address_type")
    private String type;

    @Transient
    private List<String> types;

    @Column(name = "cm_street_address")
    private String streetAddress;

    @Column(name = "cm_street_address_extra")
    private String streetAddress2;

    @Column(name = "cm_locality")
    private String city;

    @Column(name = "cm_region")
    private String state;

    @Column(name = "cm_postal_code")
    private String zip;

    @Column(name = "cm_country")
    private String country;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "acm_address_contact_method", joinColumns = {
            @JoinColumn(name = "cm_address_id", referencedColumnName = "cm_address_id")}, inverseJoinColumns = {
            @JoinColumn(name = "cm_contact_method_id", referencedColumnName = "cm_contact_method_id")})
    @OrderBy(value = "id")
    private List<ContactMethod> contactMethods;

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

    @XmlTransient
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @XmlTransient
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlTransient
    public List<String> getTypes()
    {
        return types;
    }

    public void setTypes(List<String> types)
    {
        this.types = types;
    }

    @XmlTransient
    public String getStreetAddress()
    {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    @XmlTransient
    public String getStreetAddress2()
    {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2)
    {
        this.streetAddress2 = streetAddress2;
    }

    @XmlTransient
    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    @XmlTransient
    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    @XmlTransient
    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    @XmlTransient
    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public PostalAddress returnBase()
    {
        return this;
    }

    public List<ContactMethod> getContactMethods()
    {
        return contactMethods;
    }

    public void setContactMethods(List<ContactMethod> contactMethods)
    {
        this.contactMethods = contactMethods;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PostalAddress that = (PostalAddress) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (created != null ? !created.equals(that.created) : that.created != null)
            return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null)
            return false;
        if (modified != null ? !modified.equals(that.modified) : that.modified != null)
            return false;
        if (modifier != null ? !modifier.equals(that.modifier) : that.modifier != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        if (types != null ? !types.equals(that.types) : that.types != null)
            return false;
        if (streetAddress != null ? !streetAddress.equals(that.streetAddress) : that.streetAddress != null)
            return false;
        if (streetAddress2 != null ? !streetAddress2.equals(that.streetAddress2) : that.streetAddress2 != null)
            return false;
        if (city != null ? !city.equals(that.city) : that.city != null)
            return false;
        if (state != null ? !state.equals(that.state) : that.state != null)
            return false;
        if (zip != null ? !zip.equals(that.zip) : that.zip != null)
            return false;
        return country != null ? country.equals(that.country) : that.country == null;

    }

    @Override public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (modifier != null ? modifier.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (types != null ? types.hashCode() : 0);
        result = 31 * result + (streetAddress != null ? streetAddress.hashCode() : 0);
        result = 31 * result + (streetAddress2 != null ? streetAddress2.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}
