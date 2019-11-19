package com.armedia.acm.services.billing.model;

/*-
 * #%L
 * ACM Service: Billing
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sasko.tanaskoski
 *
 */

@Entity
@Table(name = "acm_billing_item")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = BillingItem.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.billing.model.BillingItem")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class BillingItem implements Serializable, AcmObject, AcmEntity, AcmParentObjectInfo
{

    private static final long serialVersionUID = -9106468953012529578L;

    @Id
    @TableGenerator(name = "acm_billing_item_gen", table = "acm_billing_item_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_billing_item", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_billing_item_gen")
    @Column(name = "cm_billing_item_id")
    private Long id;

    @Column(name = "cm_billing_item_number")
    private Integer itemNumber;

    @Column(name = "cm_billing_item_description")
    private String itemDescription;

    @Column(name = "cm_billing_item_type")
    private String itemType;

    @Column(name = "cm_billing_item_amount")
    private Double itemAmount;

    @Column(name = "cm_parent_object_id")
    private Long parentObjectId;

    @Column(name = "cm_parent_object_type")
    private String parentObjectType;

    @Column(name = "cm_billing_item_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_billing_item_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_billing_item_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_billing_item_modifier", nullable = false)
    private String modifier;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @PrePersist
    public void beforeInsert()
    {
        Date today = new Date();
        setCreated(today);
        if (itemType == null)
        {
            setItemType(BillingConstants.BILLING_ITEM_TYPE_DEFAULT);
        }
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
     * @return the itemNumber
     */
    public Integer getItemNumber()
    {
        return itemNumber;
    }

    /**
     * @param itemNumber
     *            the itemNumber to set
     */
    public void setItemNumber(Integer itemNumber)
    {
        this.itemNumber = itemNumber;
    }

    /**
     * @return the itemDescription
     */
    public String getItemDescription()
    {
        return itemDescription;
    }

    /**
     * @param itemDescription
     *            the itemDescription to set
     */
    public void setItemDescription(String itemDescription)
    {
        this.itemDescription = itemDescription;
    }

    public String getItemType()
    {
        return itemType;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    /**
     * @return the itemAmount
     */
    public Double getItemAmount()
    {
        return itemAmount;
    }

    /**
     * @param itemAmount
     *            the itemAmount to set
     */
    public void setItemAmount(Double itemAmount)
    {
        this.itemAmount = itemAmount;
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
        return modified;
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
        return modifier;
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
        return BillingConstants.OBJECT_TYPE_ITEM;
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

}
