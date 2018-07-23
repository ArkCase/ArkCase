package com.armedia.acm.services.billing.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
@Entity
@Table(name = "acm_billing_invoice")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = BillingInvoice.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.billing.model.BillingInvoice")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class BillingInvoice implements Serializable, AcmObject, AcmEntity, AcmParentObjectInfo
{

    private static final long serialVersionUID = -8422379882933788753L;

    @Id
    @TableGenerator(name = "acm_billing_invoice_gen", table = "acm_billing_invoice_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_billing_invoice", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_billing_invoice_gen")
    @Column(name = "cm_billing_invoice_id")
    private Long id;

    @Column(name = "cm_billing_invoice_number")
    private String invoiceNumber;

    @Column(name = "cm_billing_invoice_paid_flag")
    private Boolean invoicePaidFlag;

    @Column(name = "cm_parent_object_id")
    private Long parentObjectId;

    @Column(name = "cm_parent_object_type")
    private String parentObjectType;

    @Column(name = "cm_billing_invoice_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_billing_invoice_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Transient
    private Date modified;

    @Transient
    private String modifier;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "acm_billing_invoice_item", joinColumns = {
            @JoinColumn(name = "cm_billing_invoice_id", referencedColumnName = "cm_billing_invoice_id") }, inverseJoinColumns = {
                    @JoinColumn(name = "cm_billing_item_id", referencedColumnName = "cm_billing_item_id", unique = true)
            })
    private List<BillingItem> billingItems = new ArrayList<>();

    @PrePersist
    public void beforeInsert()
    {
        Date today = new Date();
        setCreated(today);
    }

    /**
     * @return the id
     */
    @Override
    public Long getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the invoiceNumber
     */
    public String getInvoiceNumber()
    {
        return invoiceNumber;
    }

    /**
     * @param invoiceNumber
     *            the invoiceNumber to set
     */
    public void setInvoiceNumber(String invoiceNumber)
    {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * @return the invoicePaidFlag
     */
    public Boolean getInvoicePaidFlag()
    {
        return invoicePaidFlag;
    }

    /**
     * @param invoicePaidFlag
     *            the invoicePaidFlag to set
     */
    public void setInvoicePaidFlag(Boolean invoicePaidFlag)
    {
        this.invoicePaidFlag = invoicePaidFlag;
    }

    /**
     * @return the parentObjectId
     */
    @Override
    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    /**
     * @param parentObjectId
     *            the parentObjectId to set
     */
    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    /**
     * @return the parentObjectType
     */
    @Override
    public String getParentObjectType()
    {
        return parentObjectType;
    }

    /**
     * @param parentObjectType
     *            the parentObjectType to set
     */
    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    /**
     * @return the creator
     */
    @Override
    public String getCreator()
    {
        return creator;
    }

    /**
     * @param creator
     *            the creator to set
     */
    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    /**
     * @return the created
     */
    @Override
    public Date getCreated()
    {
        return created;
    }

    /**
     * @param created
     *            the created to set
     */
    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    /**
     * @return the modified
     */
    @Override
    public Date getModified()
    {
        return getCreated();
    }

    /**
     * @param modified
     *            the modified to set
     */
    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    /**
     * @return the modifier
     */
    @Override
    public String getModifier()
    {
        return creator;
    }

    /**
     * @param modifier
     *            the modifier to set
     */
    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return BillingConstants.OBJECT_TYPE_INVOICE;
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * @return the billingItems
     */
    public List<BillingItem> getBillingItems()
    {
        return billingItems;
    }

    /**
     * @param billingItems
     *            the billingItems to set
     */
    public void setBillingItems(List<BillingItem> billingItems)
    {
        this.billingItems = billingItems;
    }

}
