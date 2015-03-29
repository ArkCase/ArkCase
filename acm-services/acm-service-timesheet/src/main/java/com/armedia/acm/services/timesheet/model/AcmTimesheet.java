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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "cm_timesheet_user_id")
	private AcmUser user;
	
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
	
	@Lob
    @Column(name = "cm_timesheet_details")
    private String details;
	
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
	
	@OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_object_id")
    private List<AcmParticipant> participants = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();
	
	@PrePersist
    protected void beforeInsert()
    {
		setChildPointers();
    }
	
	@PreUpdate
    protected void beforeUpdate()
    {
		setChildPointers();
    }
	
	private void setChildPointers()
	{
		if (getTimes() != null)
		{
			for (AcmTime time : getTimes())
			{
				time.setTimesheet(this);
			}
		}
		
		if (getParticipants() != null)
		{
			for (AcmParticipant participant : getParticipants())
			{
				participant.setObjectId(getId());
				participant.setObjectType(getObjectType());
			}
		}
		
		if (getContainer() != null)
		{
			getContainer().setContainerObjectId(getId());
			getContainer().setContainerObjectType(getObjectType());
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
	
	public AcmUser getUser() {
		return user;
	}

	public void setUser(AcmUser user) {
		this.user = user;
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
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

	public List<AcmParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<AcmParticipant> participants) {
		this.participants = participants;
	}

	public AcmContainer getContainer() {
		return container;
	}

	public void setContainer(AcmContainer container) {
		this.container = container;
	}

	@Override
	@JsonIgnore
	public String getObjectType() 
	{
		return TimesheetConstants.OBJECT_TYPE;
	}
}
