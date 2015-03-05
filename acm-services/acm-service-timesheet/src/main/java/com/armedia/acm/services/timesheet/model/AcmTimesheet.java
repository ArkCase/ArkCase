/**
 * 
 */
package com.armedia.acm.services.timesheet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_timesheet")
public class AcmTimesheet implements Serializable, AcmObject, AcmEntity {

	private static final long serialVersionUID = 3346214028142786165L;

	@Id
    @Column(name = "cm_timesheet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "cm_timesheet_user_id")
	private String userId;
	
	@Column(name = "cm_timesheet_start_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name = "cm_timesheet_end_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy="timesheet")
    private List<AcmTime> times = new ArrayList<>();
	
	@Column(name = "cm_timesheet_status")
	private String status;
	
	@Column(name = "cm_timesheet_creator")
	private String creator;
	
	@Column(name = "cm_timesheet_created")
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_timesheet_modifier")
	private String modifier;
	
	@Column(name = "cm_timesheet_modified")
    @Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@PrePersist
    protected void beforeInsert()
    {
		if (getTimes() != null)
		{
			for (AcmTime time : getTimes())
			{
				time.setTimesheet(this);
			}
		}
    }
	
	@PreUpdate
    protected void beforeUpdate()
    {
		if (getTimes() != null)
		{
			for (AcmTime time : getTimes())
			{
				time.setTimesheet(this);
			}
		}
    }
	
	@Override
	public Long getId() 
	{
		return id;
	}
	
	public void setId(Long id) 
	{
		this.id = id;
	}
	
	public String getUserId() 
	{
		return userId;
	}

	public void setUserId(String userId) 
	{
		this.userId = userId;
	}
	
	public Date getStartDate() 
	{
		return startDate;
	}

	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<AcmTime> getTimes() {
		return times;
	}

	public void setTimes(List<AcmTime> times) {
		this.times = times;
	}

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
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
		return TimesheetConstants.OBJECT_TYPE;
	}
}
