/**
 * 
 */
package com.armedia.acm.services.timesheet.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

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
    @TableGenerator(name = "acm_time_gen",
            table = "acm_time_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_time",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_time_gen")
    @Column(name = "cm_time_id")
	private Long id;
	
	@JsonIgnore
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REMOVE}, optional = false)
    @JoinColumn(name="cm_time_timesheet_id") 
    private AcmTimesheet timesheet;
	
	@Column(name = "cm_time_object_id")
	private Long objectId;
	
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

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
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
	@JsonIgnore
	public String getObjectType() 
	{
		return TimeConstants.OBJECT_TYPE;
	}

}
