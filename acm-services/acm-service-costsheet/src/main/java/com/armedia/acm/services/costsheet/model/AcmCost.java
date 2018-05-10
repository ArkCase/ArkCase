/**
 * 
 */
package com.armedia.acm.services.costsheet.model;

/*-
 * #%L
 * ACM Service: Costsheet
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
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_cost")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmCost implements Serializable, AcmObject, AcmEntity
{

    private static final long serialVersionUID = 7830537295290505438L;

    @Id
    @TableGenerator(name = "acm_cost_gen", table = "acm_cost_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_cost", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_cost_gen")
    @Column(name = "cm_cost_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REMOVE }, optional = false)
    @JoinColumn(name = "cm_cost_costsheet_id")
    private AcmCostsheet costsheet;

    @Column(name = "cm_cost_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "cm_cost_value")
    private Double value;

    @Column(name = "cm_cost_title")
    private String title;

    @Column(name = "cm_cost_description")
    private String description;

    @Column(name = "cm_cost_creator")
    private String creator;

    @Column(name = "cm_cost_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_cost_modifier")
    private String modifier;

    @Column(name = "cm_cost_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public AcmCostsheet getCostsheet()
    {
        return costsheet;
    }

    public void setCostsheet(AcmCostsheet costsheet)
    {
        this.costsheet = costsheet;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Double getValue()
    {
        return value;
    }

    public void setValue(Double value)
    {
        this.value = value;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
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
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return CostConstants.OBJECT_TYPE;
    }

}
