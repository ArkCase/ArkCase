/**
 * 
 */
package com.armedia.acm.services.timesheet.model;

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
@Table(name = "acm_time")
public class AcmTime implements Serializable, AcmObject, AcmEntity {

	private static final long serialVersionUID = -7170976917435850080L;
	
	@Id
    @Column(name = "cm_time_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL}, optional = false)
    @JoinColumn(name="cm_time_timesheet_id") 
    private AcmTimesheet timesheet;
	
	@Column(name = "cm_time_code")
	private String code;
	
	@Column(name = "cm_time_type")
	private String type;
	
	@Column(name = "cm_time_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@Column(name = "cm_time_value")
	private Double value;
	
	@Column(name = "cm_time_creator")
	private String creator;
	
	@Column(name = "cm_time_created")
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_time_modifier")
	private String modifier;
	
	@Column(name = "cm_time_modified")
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

	public AcmTimesheet getTimesheet() {
		return timesheet;
	}

	public void setTimesheet(AcmTimesheet timesheet) {
		this.timesheet = timesheet;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		return TimeConstants.OBJECT_TYPE;
	}

}
