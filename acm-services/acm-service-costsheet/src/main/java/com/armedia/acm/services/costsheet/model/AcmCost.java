/**
 * 
 */
package com.armedia.acm.services.costsheet.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_cost")
public class AcmCost implements Serializable, AcmObject, AcmEntity {

	private static final long serialVersionUID = 7830537295290505438L;

	@Id
    @Column(name = "cm_cost_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL}, optional = false)
    @JoinColumn(name="cm_cost_costsheet_id") 
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

	public AcmCostsheet getCostsheet() {
		return costsheet;
	}

	public void setCostsheet(AcmCostsheet costsheet) {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
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
	public String getObjectType() 
	{
		return CostConstants.OBJECT_TYPE;
	}
	
}
